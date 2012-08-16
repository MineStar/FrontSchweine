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

import net.minecraft.server.PathEntity;
import net.minecraft.server.PathPoint;

import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;

import de.minestar.minestarlibrary.AbstractCore;
public class VinciCodeCore extends AbstractCore implements Listener {

    public static final String NAME = "VinciCode";

    private CraftLivingEntity entity = null;

    public VinciCodeCore() {
        super(NAME);
    }

    @Override
    protected boolean registerEvents(PluginManager pm) {
        pm.registerEvents(this, this);
        return true;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerInteractEvent event) {
//        List<String> pages = new ArrayList<String>();
//        pages.add(ChatColor.RED + "Willkommen auf " + ChatColor.BLUE + "Minestar.de" + ChatColor.RED + "!");
//        pages.add(ChatColor.RED + "Seite 2");
//        pages.add(ChatColor.RED + "Seite 3");
//        MinestarBook myBook = MinestarBook.createWrittenBook("AUTHOR", "TITLE", pages);
//        event.getPlayer().setItemInHand(myBook.getBukkitItemStack());

        Location loc = event.getPlayer().getLocation();

        entity = (CraftLivingEntity) loc.getWorld().spawnEntity(loc.getBlock().getRelative(10, 0, 0).getLocation(), EntityType.PIG);
        PathPoint[] path = new PathPoint[1];
        path[0] = new PathPoint(loc.getBlockX() + 1, loc.getBlockY() - 1, loc.getBlockZ());

        PathEntity pe = new PathEntity(path);
        entity.getHandle().getNavigation().a(pe, 0.5f);
        // entity.getHandle().getNavigation().a(-10, 0, 0, 0.5f);
    }
}
