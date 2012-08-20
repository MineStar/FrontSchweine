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

package de.minestar.frontschweine.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import de.minestar.frontschweine.core.FrontschweineCore;
import de.minestar.frontschweine.data.Activator;
import de.minestar.frontschweine.data.BlockVector;
import de.minestar.frontschweine.data.Line;
import de.minestar.frontschweine.data.Waypoint;
import de.minestar.minestarlibrary.utils.ConsoleUtils;

public class LineHandler {

    private HashMap<Integer, Line> linesByID = new HashMap<Integer, Line>();
    private TreeMap<String, Line> linesByName = new TreeMap<String, Line>();

    private HashMap<BlockVector, Activator> activators = new HashMap<BlockVector, Activator>();

    public void init() {
        // get lines from database
        ArrayList<Line> list = FrontschweineCore.databaseHandler.loadLines();
        if (list == null) {
            return;
        }

        // add lines
        for (Line line : list) {
            this.linesByID.put(line.getLineID(), line);
            this.linesByName.put(line.getName().toLowerCase(), line);

            // add activators
            for (Activator activator : line.getActivators().values()) {
                this.activators.put(activator.getVector(), activator);
            }
        }

        ConsoleUtils.printInfo(FrontschweineCore.NAME, this.linesByID.size() + " lines loaded!");
    }

    // ////////////////////////////////////////////////////////
    //
    // ACTIVATORS
    //
    // ////////////////////////////////////////////////////////

    public boolean hasActivator(BlockVector vector) {
        return this.activators.containsKey(vector);
    }

    public Activator getActivator(BlockVector vector) {
        return this.activators.get(vector);
    }

    public boolean addActivator(BlockVector vector, Line line, Waypoint waypoint) {
        if (this.hasActivator(vector)) {
            return false;
        }

        if (!this.hasLine(line.getName())) {
            return false;
        }

        // create the activator
        BlockVector cloneVector = vector.clone();
        Activator activator = FrontschweineCore.databaseHandler.createActivator(cloneVector, line, waypoint);
        if (activator == null) {
            return false;
        }

        this.activators.put(cloneVector, activator);
        line.addActivator(activator);
        return true;
    }

    public boolean removeActivator(BlockVector vector, Line line, Waypoint waypoint) {
        if (!this.hasActivator(vector)) {
            return false;
        }

        if (!this.hasLine(line.getName())) {
            return false;
        }

        // delete the activator from DB
        if (FrontschweineCore.databaseHandler.deleteActivator(line, vector)) {
            // delete activator from map
            line.removeActivator(vector);
            return true;
        }

        return false;
    }

    // ////////////////////////////////////////////////////////
    //
    // LINES
    //
    // ////////////////////////////////////////////////////////

    public TreeMap<String, Line> getAllLines() {
        return this.linesByName;
    }

    public boolean hasLine(String name) {
        return this.linesByName.containsKey(name.toLowerCase());
    }

    public boolean hasLine(int ID) {
        return this.linesByID.containsKey(ID);
    }

    public Line getLine(String name) {
        return this.linesByName.get(name.toLowerCase());
    }

    public Line getLine(int ID) {
        return this.linesByID.get(ID);
    }

    public boolean addLine(String name) {
        if (this.hasLine(name)) {
            return false;
        }

        // save line to DB
        Line line = FrontschweineCore.databaseHandler.addLine(name);
        if (line == null) {
            return false;
        }

        // add line to maps
        this.linesByID.put(line.getLineID(), line);
        this.linesByName.put(line.getName().toLowerCase(), line);

        return true;
    }

    public boolean removeLine(String name) {
        return this.removeLine(this.getLine(name));
    }

    public boolean removeLine(int ID) {
        return this.removeLine(this.getLine(ID));
    }

    public boolean removeLine(Line line) {
        if (line == null) {
            return false;
        }

        // delete line from DB
        if (FrontschweineCore.databaseHandler.deleteLine(line)) {
            // delete all activators from maps
            for (Activator activator : line.getActivators().values()) {
                this.activators.remove(activator.getVector());
            }
            // delete line from maps
            this.linesByID.remove(line.getLineID());
            this.linesByName.remove(line.getName().toLowerCase());
            return true;
        }
        return false;
    }

    // ////////////////////////////////////////////////////////
    //
    // WAYPOINTS
    //
    // ////////////////////////////////////////////////////////

    public boolean addWaypoint(Line line, BlockVector vector, float speed) {
        if (!this.hasLine(line.getName())) {
            return false;
        }

        // save waypoint to DB
        Waypoint waypoint = FrontschweineCore.databaseHandler.addWaypoint(line, vector, speed);
        if (waypoint == null) {
            return false;
        }

        // add waypoint to line
        line.addWaypoint(waypoint);

        return true;
    }

    public boolean updateWaypoint(Line line, int index, BlockVector vector, float speed) {
        if (!this.hasLine(line.getName())) {
            return false;
        }

        Waypoint waypoint = line.getWaypoint(index);
        if (waypoint == null) {
            return false;
        }

        // move waypoint in DB
        if (FrontschweineCore.databaseHandler.moveWaypoint(line, waypoint.getVector(), vector, speed)) {
            waypoint.getVector().update(vector.getLocation());
            waypoint.setSpeed(speed);
            return true;
        }

        return false;
    }

    public boolean removeWaypoint(Line line, int index) {
        if (!this.hasLine(line.getName())) {
            return false;
        }

        Waypoint waypoint = line.getWaypoint(index);
        if (waypoint == null) {
            return false;
        }

        // delete waypoint from DB
        if (FrontschweineCore.databaseHandler.deleteWaypoint(line, waypoint.getVector())) {
            // delete waypoint from line
            line.removeWaypoint(waypoint);
            return true;
        }

        return false;
    }
}
