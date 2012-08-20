/*
 * Copyright (C) 2012 MineStar.de 
 * 
 * This file is part of Frontschweine.
 * 
 * Frontschweine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * Frontschweine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Frontschweine.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.frontschweine.listener;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPig;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.bukkit.gemo.utils.BlockUtils;

import de.minestar.frontschweine.core.FrontschweineCore;
import de.minestar.frontschweine.data.Activator;
import de.minestar.frontschweine.data.BlockVector;
import de.minestar.frontschweine.data.Config;
import de.minestar.frontschweine.data.Line;
import de.minestar.frontschweine.data.PigData;
import de.minestar.frontschweine.data.PlayerState;
import de.minestar.frontschweine.data.TeleportThread;
import de.minestar.frontschweine.data.Waypoint;
import de.minestar.frontschweine.handler.PigHandler;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class ActionListener implements Listener {

    private BlockVector vector = new BlockVector("", 0, 0, 0);
    private PigHandler pigHandler;

    public ActionListener(PigHandler pigHandler) {
        this.pigHandler = pigHandler;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        // only if the player is riding a pig
        if (event.getPlayer().isInsideVehicle() && event.getPlayer().getVehicle().getType().equals(EntityType.PIG)) {
            // only check if the block is different
            if (BlockUtils.LocationEquals(event.getTo(), event.getFrom())) {
                return;
            }

            // try to get the pig
            PigData pigData = this.pigHandler.getPigDataByPlayer(event.getPlayer());
            if (pigData == null || !pigData.getUUID().equals(event.getPlayer().getVehicle().getUniqueId())) {
                return;
            }

            // update the pig
            pigData.update(event.getTo());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onVehicleExit(VehicleExitEvent event) {
        // ONLY PIGS & PLAYERS ARE ENABLED
        if (!event.getVehicle().getType().equals(EntityType.PIG) || !event.getExited().getType().equals(EntityType.PLAYER)) {
            return;
        }

        // try to get the pig
        PigData pigData = this.pigHandler.getPigDataByUUID(event.getVehicle());
        if (pigData == null || !pigData.getUUID().equals(event.getVehicle().getUniqueId())) {
            return;
        }

        // exit the pig and remove the pig
        this.pigHandler.removePig(pigData);

        Location currentLocation = event.getVehicle().getLocation().clone();
        pigData.exit(false);
        if (event.getVehicle().getPassenger() != null) {
            // TP THE PLAYER
            TeleportThread tpThread = new TeleportThread(((Player) event.getVehicle().getPassenger()).getName(), currentLocation);
            Bukkit.getScheduler().scheduleSyncDelayedTask(FrontschweineCore.INSTANCE, tpThread, 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (this.handleDamage(event.getEntity())) {
            event.setDamage(0);
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (this.handleDamage(event.getEntity())) {
            event.setDamage(0);
            event.setCancelled(true);
        }
    }

    private boolean handleDamage(Entity entity) {
        // only pigs are affected
        if (!entity.getType().equals(EntityType.PIG)) {
            return false;
        }

        // no damage on handled pigsS
        return this.pigHandler.hasPigDataByUUID(entity);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerKick(PlayerKickEvent event) {
        this.handleDisconnect(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        this.handleDisconnect(event.getPlayer());
    }

    private void handleDisconnect(Player player) {
        if (this.pigHandler.hasPigDataByPlayer(player)) {
            PigData pigData = this.pigHandler.getPigDataByPlayer(player);
            // exit the pig and remove the pig
            this.pigHandler.removePig(pigData);
            pigData.exit(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.pigHandler.hasPigDataByPlayer(event.getEntity())) {
            PigData pigData = this.pigHandler.getPigDataByPlayer(event.getEntity());
            // exit the pig and remove the pig
            this.pigHandler.removePig(pigData);
            pigData.exit(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final PlayerState state = FrontschweineCore.playerHandler.getData(event.getPlayer().getName()).getState();
        switch (state) {
            case ACTIVATOR_ADD : {
                this.handleActivatorAdd(event);
                break;
            }
            case ACTIVATOR_REMOVE : {
                this.handleActivatorRemove(event);
                break;
            }
            default : {
                this.handleNormalInteract(event);
                break;
            }
        }

    }

    private void handleActivatorAdd(PlayerInteractEvent event) {
        // cancel the event
        event.setCancelled(true);
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        if (event.getClickedBlock().getTypeId() == Material.STONE_BUTTON.getId()) {
            this.vector.update(event.getClickedBlock().getLocation());
            Activator activator = FrontschweineCore.lineHandler.getActivator(vector);

            if (activator == null) {
                Line line = FrontschweineCore.playerHandler.getData(event.getPlayer().getName()).getLine();
                Waypoint waypoint = FrontschweineCore.playerHandler.getData(event.getPlayer().getName()).getWaypoint();
                if (FrontschweineCore.lineHandler.addActivator(vector.clone(), line, waypoint, FrontschweineCore.playerHandler.getData(event.getPlayer().getName()).isBackwards())) {
                    PlayerUtils.sendSuccess(event.getPlayer(), FrontschweineCore.NAME, "Aktivierer gespeichert.");
                } else {
                    PlayerUtils.sendError(event.getPlayer(), FrontschweineCore.NAME, "Aktivierer konnte nicht gespeichert werden!");
                }
            } else {
                PlayerUtils.sendError(event.getPlayer(), FrontschweineCore.NAME, "Der Block ist bereits ein Aktivierer!");
            }
        } else {
            PlayerUtils.sendError(event.getPlayer(), FrontschweineCore.NAME, "Aktivierer können nur Stone-Buttons sein!");
        }

        // update
        FrontschweineCore.playerHandler.getData(event.getPlayer().getName()).update(null, null, false);
        FrontschweineCore.playerHandler.setState(event.getPlayer().getName(), PlayerState.NORMAL);
    }

    private void handleActivatorRemove(PlayerInteractEvent event) {
        // cancel the event
        event.setCancelled(true);
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        if (event.getClickedBlock().getTypeId() == Material.STONE_BUTTON.getId()) {
            this.vector.update(event.getClickedBlock().getLocation());
            Activator activator = FrontschweineCore.lineHandler.getActivator(vector);

            if (activator != null) {
                Line line = FrontschweineCore.lineHandler.getLine(activator.getLineID());
                if (line != null) {
                    if (FrontschweineCore.lineHandler.removeActivator(vector, line, activator.getWaypoint())) {
                        PlayerUtils.sendSuccess(event.getPlayer(), FrontschweineCore.NAME, "Aktivierer entfernt.");
                    } else {
                        PlayerUtils.sendError(event.getPlayer(), FrontschweineCore.NAME, "Aktivierer konnte nicht gelöscht werden!");
                    }
                } else {
                    PlayerUtils.sendError(event.getPlayer(), FrontschweineCore.NAME, "Linie nicht gefunden!");
                }
            } else {
                PlayerUtils.sendError(event.getPlayer(), FrontschweineCore.NAME, "Der Block ist kein Aktivierer!");
            }
        } else {
            PlayerUtils.sendError(event.getPlayer(), FrontschweineCore.NAME, "Der Block ist kein Aktivierer!");
        }

        // update
        FrontschweineCore.playerHandler.getData(event.getPlayer().getName()).update(null, null, false);
        FrontschweineCore.playerHandler.setState(event.getPlayer().getName(), PlayerState.NORMAL);
    }

    private CraftPig getFreePig() {
        if (Config.PIG_VECTOR == null) {
            return null;
        }

        Collection<Pig> pigList = Config.PIG_VECTOR.getLocation().getWorld().getEntitiesByClass(Pig.class);
        for (Pig pig : pigList) {
            if (!pig.hasSaddle() && pig.getPassenger() == null) {
                if (Config.PIG_VECTOR.getLocation().distance(pig.getLocation()) < Config.CHECK_RADIUS) {
                    return (CraftPig) pig;
                }
            }
        }
        return null;
    }

    private void handleNormalInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock().getTypeId() != Material.STONE_BUTTON.getId()) {
            return;
        }

        if (!event.getPlayer().isInsideVehicle() && !this.pigHandler.hasPigDataByPlayer(event.getPlayer())) {
            this.vector.update(event.getClickedBlock().getLocation());
            Activator activator = FrontschweineCore.lineHandler.getActivator(vector);
            if (activator != null) {
                Line line = FrontschweineCore.lineHandler.getLine(activator.getLineID());
                if (line != null) {
                    CraftPig pig = this.getFreePig();
                    if (pig != null) {
                        pig.teleport(activator.getWaypoint().getLocation());
                        event.getPlayer().teleport(pig);
                        pig.setSaddle(true);
                        pig.setPassenger(event.getPlayer());
                        PigData pigData = new PigData(event.getPlayer().getName(), pig, line.getPath(), line.isLoop(), activator.isBackwards());
                        pigHandler.addPigData(pigData);
                        pigData.setWaypoint(activator.getWaypoint().getPlaceInLine());
                        pigData.update(event.getPlayer().getLocation());
                        PlayerUtils.sendMessage(event.getPlayer(), ChatColor.AQUA, "Herzlich willkommen auf der Linie " + ChatColor.RED + "'" + line.getName() + "'" + ChatColor.AQUA + "!");
                    } else {
                        PlayerUtils.sendError(event.getPlayer(), FrontschweineCore.NAME, "Tut uns leid. Leider gerade kein Schwein für Sie Zeit. :{");
                    }
                } else {
                    PlayerUtils.sendError(event.getPlayer(), FrontschweineCore.NAME, "Die Linie konnte nicht gefunden werden!");
                }
            }
        }
    }
}
