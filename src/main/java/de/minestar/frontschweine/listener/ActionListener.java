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

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPig;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.bukkit.gemo.utils.BlockUtils;

import de.minestar.frontschweine.data.Path;
import de.minestar.frontschweine.data.PigData;
import de.minestar.frontschweine.data.Waypoint;
import de.minestar.frontschweine.handler.PigHandler;

public class ActionListener implements Listener {

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
        pigData.exit();
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
        if (!entity.equals(EntityType.PIG)) {
            return false;
        }

        // no damage on handled pigs
        if (this.pigHandler.hasPigDataByUUID(entity)) {
            return true;
        }

        return false;
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
            pigData.exit();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (this.pigHandler.hasPigDataByPlayer(event.getEntity())) {
            PigData pigData = this.pigHandler.getPigDataByPlayer(event.getEntity());
            // exit the pig and remove the pig
            this.pigHandler.removePig(pigData);
            pigData.exit();
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking() && !event.getPlayer().isInsideVehicle() && !this.pigHandler.hasPigDataByPlayer(event.getPlayer())) {
            Location loc = event.getPlayer().getLocation();

            Path path = new Path();
            path.addWaypoint(new Waypoint(loc.getBlock().getRelative(-20, 0, -20).getLocation(), 0.5f));
            path.addWaypoint(new Waypoint(loc.getBlock().getRelative(+20, 0, -20).getLocation(), 0.5f));
            path.addWaypoint(new Waypoint(loc.getBlock().getRelative(+20, 0, +20).getLocation(), 0.5f));
            path.addWaypoint(new Waypoint(loc.getBlock().getRelative(-20, 0, +20).getLocation(), 0.5f));
            path.addWaypoint(new Waypoint(loc.getBlock().getRelative(-19, 0, -20).getLocation(), 0.5f));
            path.addWaypoint(new Waypoint(loc, 0.5f));

            // create pig
            CraftPig pigEntity = (CraftPig) loc.getWorld().spawnEntity(loc.getBlock().getRelative(0, 0, 0).getLocation(), EntityType.PIG);

            // player should "enter the pig"
            pigEntity.setSaddle(true);
            pigEntity.setPassenger(event.getPlayer());

            PigData pigData = new PigData(event.getPlayer().getName(), pigEntity, path);
            pigData.start();

            // save the pig to a map
            this.pigHandler.addPigData(pigData);
        }
    }

}
