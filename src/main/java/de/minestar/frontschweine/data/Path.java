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

import java.util.ArrayList;

public class Path {
    private ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();

    public int getSize() {
        return this.waypoints.size();
    }

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public Waypoint getWaypoint(int index) {
        return this.waypoints.get(index);
    }

    public void addWaypoints(ArrayList<Waypoint> waypoints) {
        for (Waypoint vector : waypoints) {
            this.addWaypoint(vector);
        }
    }

    public void addWaypoints(Waypoint... waypoints) {
        for (Waypoint vector : waypoints) {
            this.addWaypoint(vector);
        }
    }

    public void addWaypoint(Waypoint waypoint) {
        if (!this.hasWaypoint(waypoint)) {
            this.waypoints.add(waypoint);
        }
    }

    public boolean hasWaypoint(Waypoint waypoint) {
        for (int i = 0; i < this.waypoints.size(); i++) {
            if (waypoint.equals(this.waypoints.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void removeWaypoints(ArrayList<Waypoint> waypoints) {
        for (Waypoint waypoint : waypoints) {
            this.removeWaypoint(waypoint);
        }
    }

    public void removeWaypoints(Waypoint... waypoints) {
        for (Waypoint waypoint : waypoints) {
            this.removeWaypoint(waypoint);
        }
    }
    public void removeWaypoint(BlockVector vector) {
        Waypoint waypoint = null;
        for (Waypoint wp : waypoints) {
            if (wp.getVector().equals(vector)) {
                waypoint = wp;
                break;
            }
        }
        if (waypoint != null) {
            this.removeWaypoint(waypoint);
        }
    }

    public void removeWaypoint(Waypoint waypoint) {
        boolean found = false;
        int deleteIndex = -1;
        for (int index = 0; index < this.waypoints.size(); index++) {
            if (found) {
                this.waypoints.get(index).setPlaceInLine(this.waypoints.get(index).getPlaceInLine() - 1);
            } else if (waypoint.equals(this.waypoints.get(index))) {
                deleteIndex = index;
                found = true;
            }
        }

        if (found) {
            this.waypoints.remove(deleteIndex);
        }
    }
}
