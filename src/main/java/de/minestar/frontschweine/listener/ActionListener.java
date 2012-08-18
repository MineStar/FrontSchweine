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

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPig;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.bukkit.gemo.utils.BlockUtils;

import de.minestar.frontschweine.data.Path;
import de.minestar.frontschweine.data.PigData;
import de.minestar.frontschweine.data.Waypoint;

public class ActionListener implements Listener {

    public HashMap<String, PigData> pigMap = new HashMap<String, PigData>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // only if the player is riding a pig
        if (event.getPlayer().isInsideVehicle() && event.getPlayer().getVehicle().getType().equals(EntityType.PIG)) {
            // only check if the block is different
            if (BlockUtils.LocationEquals(event.getTo(), event.getFrom())) {
                return;
            }

            // try to get the pig
            PigData pigData = pigMap.get(event.getPlayer().getName());
            if (pigData == null) {
                return;
            }

            pigData.update(event.getTo());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking() && !event.getPlayer().isInsideVehicle()) {
            Location loc = event.getPlayer().getLocation();

            Path path = new Path();
            path.addWaypoint(new Waypoint(loc.getBlock().getRelative(-20, 0, -20).getLocation()));
            path.addWaypoint(new Waypoint(loc.getBlock().getRelative(+20, 0, -20).getLocation()));
            path.addWaypoint(new Waypoint(loc.getBlock().getRelative(+20, 0, +20).getLocation()));
            path.addWaypoint(new Waypoint(loc.getBlock().getRelative(-20, 0, +20).getLocation()));
            path.addWaypoint(new Waypoint(loc.getBlock().getRelative(-19, 0, -20).getLocation()));
            path.addWaypoint(new Waypoint(loc));

            // create pig
            CraftPig pigEntity = (CraftPig) loc.getWorld().spawnEntity(loc.getBlock().getRelative(0, 0, 0).getLocation(), EntityType.PIG);

            // player should "enter the pig"
            pigEntity.setSaddle(true);
            pigEntity.setPassenger(event.getPlayer());

            PigData pigData = new PigData(pigEntity, path);
            pigData.start();

            // save the pig to a map
            pigMap.put(event.getPlayer().getName(), pigData);
        }
    }

}
