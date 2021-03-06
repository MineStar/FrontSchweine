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

import net.minecraft.server.v1_5_R2.PathEntity;
import net.minecraft.server.v1_5_R2.Vec3D;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_5_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_5_R2.entity.CraftPig;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import de.minestar.minestarlibrary.utils.PlayerUtils;

public class PigData {
    private final String playerName;
    private final CraftPig pig;
    private Vec3D waypointVec3D;
    private PathEntity pathEntity;
    private int currentWaypointIndex;
    private Waypoint currentWaypoint;
    private Path path;
    private boolean isLoop;
    private boolean isBackwards;

    private boolean isWaiting = false;
    private boolean messageSend = false;

    public PigData(String playerName, CraftPig pig, Path path, boolean isLoop, boolean isBackwards) {
        this.playerName = playerName;
        this.pig = pig;
        this.isLoop = isLoop;
        this.isBackwards = isBackwards;
        this.setPath(path);
    }

    public String getPlayerName() {
        return playerName;
    }

    public UUID getUUID() {
        return this.pig.getUniqueId();
    }

    public void setWaypoint(int index) {
        if (index < 0 || index > (this.path.getSize() - 1)) {
            return;
        }

        this.currentWaypointIndex = index;
        this.currentWaypoint = this.path.getWaypoint(this.currentWaypointIndex);
        this.refreshPath();
    }

    public void setWaiting(boolean isWaiting) {
        this.isWaiting = isWaiting;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    private float getLastWaypointSpeed() {
        if (!this.isBackwards) {
            if (this.currentWaypointIndex > 1) {
                return this.path.getWaypoint(this.currentWaypointIndex - 1).getSpeed();
            } else {
                if (this.path.getSize() > 0) {
                    return this.path.getWaypoint(0).getSpeed();
                } else {
                    return 0.4f;
                }
            }
        } else {
            if (this.currentWaypointIndex < this.path.getSize() - 1) {
                return this.path.getWaypoint(this.currentWaypointIndex + 1).getSpeed();
            } else {
                if (this.path.getSize() > 0) {
                    return this.path.getWaypoint(this.path.getSize() - 1).getSpeed();
                } else {
                    return 0.4f;
                }
            }
        }
    }

    public int getCurrentWaypointIndex() {
        return currentWaypointIndex;
    }

    public void update(Location location) {
        if (this.currentWaypoint == null) {
            this.exit(true);
            return;
        }

        /**
         * CHECK ==>> does the pig have the same goal as we defined it? If not,
         * refresh it to the current path. This way pigs can not "forget" the
         * current waypoint
         */
        if (!this.isSameGoal(this.pig.getHandle().getNavigation().d())) {
            this.refreshPath();
        }

        double distance = Math.abs(location.distance(this.currentWaypoint.getLocation()));
        if (distance < 1.5d) {
            this.onWaypointReached();
        }
    }

    private boolean isSameGoal(PathEntity pigPathEntity) {
        if (pigPathEntity == null || this.waypointVec3D == null)
            return false;
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

    public void refreshPath() {
        if (this.currentWaypoint == null) {
            this.exit(true);
        }

        // find the path
        Location target = this.currentWaypoint.getLocation();
        int searchDistance = (int) Math.abs(target.distance(this.pig.getLocation())) + 20;
        this.pathEntity = ((CraftWorld) target.getWorld()).getHandle().a(this.pig.getHandle(), target.getBlockX(), target.getBlockY(), target.getBlockZ(), searchDistance, false, false, false, true);
        this.waypointVec3D = Vec3D.a(this.currentWaypoint.getX(), this.currentWaypoint.getY(), this.currentWaypoint.getZ());

        // set the path & speed
        this.pig.getHandle().getNavigation().a(pathEntity, this.getLastWaypointSpeed());
    }

    private void onWaypointReached() {
        if (!isBackwards) {
            // FORWARD
            if (this.currentWaypointIndex + 1 == this.path.getSize()) {
                if (!isLoop) {
                    // reached the final waypoint
                    if (this.pig.getPassenger() != null && this.pig.getPassenger().getType() == EntityType.PLAYER) {
                        PlayerUtils.sendMessage((Player) this.pig.getPassenger(), ChatColor.AQUA, "Die " + ChatColor.RED + "UVB" + ChatColor.AQUA + " bedanken sich f�r diesen schweinischen Ritt!");
                    }
                    this.exit(true);
                } else {
                    // back to start
                    if (!this.currentWaypoint.isWaiting()) {
                        this.setWaypoint(0);
                        this.setWaiting(false);
                        this.messageSend = false;
                    } else {
                        this.setWaypoint(this.currentWaypointIndex);
                        Player player = Bukkit.getPlayer(this.playerName);
                        if (player != null && player.isOnline() && !messageSend) {
                            PlayerUtils.sendMessage(player, ChatColor.GRAY, "Wegpunkt erreicht. Interagiere um forzufahren.");
                            messageSend = true;
                        }
                        this.setWaiting(true);
                    }
                }
            } else {
                // reached a normal waypoint
                if (!this.currentWaypoint.isWaiting()) {
                    this.setWaypoint(this.currentWaypointIndex + 1);
                    this.setWaiting(false);
                    this.messageSend = false;
                } else {
                    this.setWaypoint(this.currentWaypointIndex);
                    Player player = Bukkit.getPlayer(this.playerName);
                    if (player != null && player.isOnline() && !messageSend) {
                        PlayerUtils.sendMessage(player, ChatColor.GRAY, "Wegpunkt erreicht. Interagiere um forzufahren.");
                        messageSend = true;
                    }
                    this.setWaiting(true);
                }
            }
        } else {
            // BACKWARD
            if (this.currentWaypointIndex - 1 < 0) {
                if (!isLoop) {
                    // reached the final waypoint
                    if (this.pig.getPassenger() != null && this.pig.getPassenger().getType() == EntityType.PLAYER) {
                        PlayerUtils.sendMessage((Player) this.pig.getPassenger(), ChatColor.AQUA, "Die " + ChatColor.RED + "UVB" + ChatColor.AQUA + " bedanken sich f�r diesen schweinischen Ritt!");
                    }
                    this.exit(true);
                } else {
                    // back to start
                    if (!this.currentWaypoint.isWaiting()) {
                        this.setWaypoint(this.path.getSize() - 1);
                        this.setWaiting(false);
                        this.messageSend = false;
                    } else {
                        this.setWaypoint(this.currentWaypointIndex);
                        Player player = Bukkit.getPlayer(this.playerName);
                        if (player != null && player.isOnline() && !messageSend) {
                            PlayerUtils.sendMessage(player, ChatColor.GRAY, "Wegpunkt erreicht. Interagiere um forzufahren.");
                            messageSend = true;
                        }
                        this.setWaiting(true);
                    }
                }
            } else {
                // reached a normal waypoint
                if (!this.currentWaypoint.isWaiting()) {
                    this.setWaypoint(this.currentWaypointIndex - 1);
                    this.setWaiting(false);
                    this.messageSend = false;
                } else {
                    this.setWaypoint(this.currentWaypointIndex);
                    Player player = Bukkit.getPlayer(this.playerName);
                    if (player != null && player.isOnline() && !messageSend) {
                        PlayerUtils.sendMessage(player, ChatColor.GRAY, "Wegpunkt erreicht. Interagiere um forzufahren.");
                        messageSend = true;
                    }
                    this.setWaiting(true);
                }
            }
        }
    }
    public void exit(boolean ejectPlayer) {
        // eject the player and remove the pig
        if (ejectPlayer && this.pig.getPassenger() != null) {
            this.pig.eject();
        }
        this.pig.setSaddle(false);

        // teleport the pig back
        this.pig.teleport(Config.PIG_VECTOR.getLocation());

        PathEntity pathEntity = ((CraftWorld) this.pig.getWorld()).getHandle().a(this.pig.getHandle(), pig.getLocation().getBlockX(), pig.getLocation().getBlockY(), pig.getLocation().getBlockZ(), 5, false, false, false, true);

        // set the path & speed
        this.pig.getHandle().getNavigation().a(pathEntity, 0.2f);

        // clean up
        this.cleanUp();
    }

    public void nextWaypoint() {
        this.setWaiting(false);
        this.messageSend = false;
        if (!isBackwards) {
            // FORWARD
            if (this.currentWaypointIndex + 1 == this.path.getSize()) {
                if (!isLoop) {
                    // reached the final waypoint
                    if (this.pig.getPassenger() != null && this.pig.getPassenger().getType() == EntityType.PLAYER) {
                        PlayerUtils.sendMessage((Player) this.pig.getPassenger(), ChatColor.AQUA, "Die " + ChatColor.RED + "UVB" + ChatColor.AQUA + " bedanken sich f�r diesen schweinischen Ritt!");
                    }
                    this.exit(true);
                } else {
                    // back to start
                    this.setWaypoint(0);
                }
            } else {
                // reached a normal waypoint
                this.setWaypoint(this.currentWaypointIndex + 1);
            }
        } else {
            // BACKWARD
            if (this.currentWaypointIndex - 1 < 0) {
                if (!isLoop) {
                    // reached the final waypoint
                    if (this.pig.getPassenger() != null && this.pig.getPassenger().getType() == EntityType.PLAYER) {
                        PlayerUtils.sendMessage((Player) this.pig.getPassenger(), ChatColor.AQUA, "Die " + ChatColor.RED + "UVB" + ChatColor.AQUA + " bedanken sich f�r diesen schweinischen Ritt!");
                    }
                    this.exit(true);
                } else {
                    // back to start
                    this.setWaypoint(this.path.getSize() - 1);
                }
            } else {
                // reached a normal waypoint
                this.setWaypoint(this.currentWaypointIndex - 1);
            }
        }
    }
}
