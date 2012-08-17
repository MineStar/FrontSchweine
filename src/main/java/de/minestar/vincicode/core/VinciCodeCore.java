/*
 * Copyright (C) 2012 MineStar.de 
 * 
 * This file is part of VinciCode.
 * 
 * VinciCode is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * 
 * VinciCode is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with VinciCode.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.minestar.vincicode.core;

import java.util.HashMap;
import java.util.List;

import net.minecraft.server.PathEntity;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPig;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginManager;

import com.bukkit.gemo.utils.BlockUtils;

import de.minestar.minestarlibrary.AbstractCore;
public class VinciCodeCore extends AbstractCore implements Listener {

    public static final String NAME = "VinciCode";

    public HashMap<String, CraftPig> pigMap = new HashMap<String, CraftPig>();

    public VinciCodeCore() {
        super(NAME);
    }

    @Override
    protected boolean registerEvents(PluginManager pm) {
        pm.registerEvents(this, this);
        return true;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // only if the player is riding a pig
        if (event.getPlayer().isInsideVehicle() && event.getPlayer().getVehicle().getType().equals(EntityType.PIG)) {
            // only check if the block is different
            if (BlockUtils.LocationEquals(event.getTo(), event.getFrom())) {
                return;
            }

            // try to get the pig
            CraftPig pig = pigMap.get(event.getPlayer().getName());
            if (pig == null) {
                return;
            }

            // get the saved target-waypoint
            if (!pig.hasMetadata("wp_target")) {
                return;
            }

            // create the location
            List<MetadataValue> values = pig.getMetadata("wp_target");
            Location t = BlockUtils.LocationFromString(values.get(0).asString());

            // check if the goal is reached => eject the player, clear the map
            // and remove the pig
            if (BlockUtils.LocationEquals(t, event.getTo())) {
                event.getPlayer().getVehicle().eject();
                this.pigMap.remove(event.getPlayer().getName());
                pig.remove();
                return;
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
//        List<String> pages = new ArrayList<String>();
//        pages.add(ChatColor.RED + "Willkommen auf " + ChatColor.BLUE + "Minestar.de" + ChatColor.RED + "!");
//        pages.add(ChatColor.RED + "Seite 2");
//        pages.add(ChatColor.RED + "Seite 3");
//        MinestarBook myBook = MinestarBook.createWrittenBook("AUTHOR", "TITLE", pages);
//        event.getPlayer().setItemInHand(myBook.getBukkitItemStack());

        if (event.getPlayer().isSneaking() && !event.getPlayer().isInsideVehicle()) {
            Location loc = event.getPlayer().getLocation();

            // create pig
            CraftPig pigEntity = (CraftPig) loc.getWorld().spawnEntity(loc.getBlock().getRelative(1, 0, 1).getLocation(), EntityType.PIG);

            // find the path
            Location target = loc.getBlock().getRelative(20, 0, 20).getLocation().clone();
            PathEntity pe = ((CraftWorld) loc.getWorld()).getHandle().a(pigEntity.getHandle(), target.getBlockX(), loc.getBlockY(), target.getBlockZ(), 150, true, true, true, true);

            // set the path & speed
            pigEntity.getHandle().getNavigation().a(pe, 0.5f);

            // player should "enter the pig"
            pigEntity.setSaddle(true);
            pigEntity.setPassenger(event.getPlayer());

            // set the target-waypoint
            pigEntity.setMetadata("wp_target", new FixedMetadataValue(this, BlockUtils.LocationToString(target)));

            // save the pig to a map
            pigMap.put(event.getPlayer().getName(), pigEntity);
        }
    }

}
