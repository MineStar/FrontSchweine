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

import java.util.ArrayList;
import java.util.HashMap;

public class Line {

    private final int lineID;
    private final HashMap<BlockVector, Activator> activators = new HashMap<BlockVector, Activator>();
    private final Path path = new Path();

    public Line(int lineID) {
        this.lineID = lineID;
    }

    public int getLineID() {
        return lineID;
    }

    // /////////////////////////////////////////
    //
    // ACTIVATOR
    //
    // /////////////////////////////////////////

    public Activator getActivator(BlockVector vector) {
        return this.activators.get(vector);
    }

    public void addActivator(Activator activator) {
        this.activators.put(activator.getVector(), activator);
    }

    public boolean hasActivator(Activator activator) {
        return this.activators.containsKey(activator.getVector());
    }

    public boolean hasActivator(BlockVector vector) {
        return this.activators.containsKey(vector);
    }

    // /////////////////////////////////////////
    //
    // PATH
    //
    // /////////////////////////////////////////

    public Path getPath() {
        return path;
    }

    public int getPathSize() {
        return this.path.getSize();
    }

    public Waypoint getWaypoint(int index) {
        return this.path.getWaypoint(index);
    }

    public void addWaypoints(ArrayList<Waypoint> waypoints) {
        this.path.addWaypoints(waypoints);
    }

    public void addWaypoints(Waypoint... waypoints) {
        this.path.addWaypoints(waypoints);
    }

    public void addWaypoint(Waypoint waypoint) {
        this.addWaypoint(waypoint);
    }

    public boolean hasWaypoint(Waypoint waypoint) {
        return this.path.hasWaypoint(waypoint);
    }

    public void removeWaypoints(ArrayList<Waypoint> waypoints) {
        this.path.removeWaypoints(waypoints);
    }

    public void removeWaypoints(Waypoint... waypoints) {
        this.path.removeWaypoints(waypoints);
    }

    public void removeWaypoint(Waypoint waypoint) {
        this.path.removeWaypoint(waypoint);
    }
}
