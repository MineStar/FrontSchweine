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

package de.minestar.frontschweine.data;

import java.util.UUID;

import net.minecraft.server.PathEntity;
import net.minecraft.server.Vec3D;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPig;

public class PigData {
    private final String playerName;
    private final CraftPig pig;
    private Vec3D waypointVec3D;
    private PathEntity pathEntity;
    private int currentWaypointIndex;
    private Waypoint currentWaypoint;
    private Path path;

    public PigData(String playerName, CraftPig pig, Path path) {
        this.playerName = playerName;
        this.pig = pig;
        this.setPath(path);
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUUID() {
        return this.pig.getUniqueId();
    }

    public void start() {
        if (this.path.getSize() > 0) {
            this.currentWaypointIndex = 0;
            this.currentWaypoint = this.path.getWaypoint(this.currentWaypointIndex);
            this.refreshPath();
        }
    }

    public void update(Location location) {
        if (this.currentWaypoint == null) {
            this.exit();
            return;
        }

        /**
         * CHECK ==>> does the pig have the same goal as we defined it? If not,
         * refresh it to the current path. This way pigs can not "forget" the
         * current waypoint
         */
        if (!this.isSameGoal(this.pig.getHandle().getNavigation().d())) {
            System.out.println("pig forgot the path => refreshing it");
            this.refreshPath();
        }

        double distance = Math.abs(location.distance(this.currentWaypoint.getLocation()));
        if (distance < 1.5d) {
            this.onWaypointReached();
        }
    }

    private boolean isSameGoal(PathEntity pigPathEntity) {
        return pigPathEntity.b(this.waypointVec3D);
    }

    private void setPath(Path path) {
        this.path = path;
        this.cleanUp();
    }

    private void cleanUp() {
        this.currentWaypointIndex = -1;
        this.currentWaypoint = null;
        this.waypointVec3D = null;
        this.pathEntity = null;
    }

    private void refreshPath() {
        if (this.currentWaypoint == null) {
            this.exit();
        }

        // find the path
        Location target = this.currentWaypoint.getLocation();
        int searchDistance = (int) Math.abs(target.distance(this.pig.getLocation())) + 20;
        this.pathEntity = ((CraftWorld) target.getWorld()).getHandle().a(this.pig.getHandle(), target.getBlockX(), target.getBlockY(), target.getBlockZ(), searchDistance, false, false, false, true);
        this.waypointVec3D = Vec3D.a(this.currentWaypoint.getX(), this.currentWaypoint.getY(), this.currentWaypoint.getZ());

        // set the path & speed
        this.pig.getHandle().getNavigation().a(pathEntity, this.currentWaypoint.getSpeed());
    }

    private void onWaypointReached() {
        if (this.currentWaypointIndex + 1 == this.path.getSize()) {
            // reached the final waypoint
            System.out.println("final waypoint reached");
            System.out.println("----------------------");
            this.exit();
        } else {
            System.out.println("waypoint " + (this.currentWaypointIndex + 1) + " of " + this.path.getSize() + " reached");
            // reached a normal waypoint
            this.currentWaypointIndex++;
            this.currentWaypoint = this.path.getWaypoint(this.currentWaypointIndex);
            this.refreshPath();
        }
    }

    public void exit() {
        // eject the player and remove the pig
        this.pig.eject();
        this.pig.setSaddle(false);
        this.pig.remove();

        // clean up
        this.cleanUp();
    }
}
