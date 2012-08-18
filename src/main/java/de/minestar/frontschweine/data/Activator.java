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

package de.minestar.frontschweine.data;

public class Activator {
    private final int ID;
    private final int lineID;
    private final Waypoint waypoint;
    private final BlockVector vector;

    public Activator(int ID, int lineID, Waypoint waypoint, BlockVector vector) {
        this.ID = ID;
        this.lineID = lineID;
        this.waypoint = waypoint;
        this.vector = vector;
    }

    public int getID() {
        return ID;
    }

    public int getLineID() {
        return lineID;
    }

    public Waypoint getWaypoint() {
        return waypoint;
    }

    public BlockVector getVector() {
        return vector;
    }
}
