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

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;

import de.minestar.frontschweine.data.Activator;
import de.minestar.frontschweine.data.BlockVector;
import de.minestar.frontschweine.data.Line;
import de.minestar.frontschweine.data.Path;
import de.minestar.frontschweine.data.Waypoint;
import de.minestar.minestarlibrary.database.AbstractMySQLHandler;
import de.minestar.minestarlibrary.database.DatabaseUtils;

public class DatabaseHandler extends AbstractMySQLHandler {

    private PreparedStatement addLine, loadLines, deleteLine, getLineByName;
    private PreparedStatement addWaypoint, loadWaypointsForLine, deleteWaypoint, deleteWaypointsForLine, getWaypointAt, updateWaypoint;
    private PreparedStatement addActivator, loadActivatorsForLine, deleteActivator, deleteActivatorsForLine, getActivatorAtPosition;

    public DatabaseHandler(String pluginName, File SQLConfigFile) {
        super(pluginName, SQLConfigFile);
    }

    @Override
    protected void createStructure(String pluginName, Connection con) throws Exception {
        DatabaseUtils.createStructure(DatabaseHandler.class.getResourceAsStream("/structure.sql"), con, pluginName);
    }

    @Override
    protected void createStatements(String pluginName, Connection con) throws Exception {
        // LINES
        this.addLine = con.prepareStatement("INSERT INTO `lines` (`name`, `isLoop`) VALUES (?, ?)");
        this.deleteLine = con.prepareStatement("DELETE FROM `lines` WHERE `name`=?");
        this.loadLines = con.prepareStatement("SELECT * FROM `lines` ORDER BY `ID` ASC");
        this.getLineByName = con.prepareStatement("SELECT * FROM `lines` WHERE `name`=?");

        // WAYPOINTS
        this.addWaypoint = con.prepareStatement("INSERT INTO `waypoints` (`lineID`, `x`, `y`, `z`, `world`, `speed`) VALUES (?, ?, ?, ?, ?, ?)");
        this.deleteWaypoint = con.prepareStatement("DELETE FROM `waypoints` WHERE `lineID`=? AND `x`=? AND `y`=? AND `z`=? AND `world`=?");
        this.loadWaypointsForLine = con.prepareStatement("SELECT * FROM `waypoints` WHERE `lineID`=? ORDER BY `ID` ASC");
        this.deleteWaypointsForLine = con.prepareStatement("DELETE FROM `waypoints` WHERE `lineID`=?");
        this.getWaypointAt = con.prepareStatement("SELECT * FROM `waypoints` WHERE `lineID`=? AND `x`=? AND `y`=? AND `z`=? AND `world`=?");
        this.updateWaypoint = con.prepareStatement("UPDATE `waypoints` SET `x`=? , `y`=? , `z`=? , `world`=? , `speed`=? WHERE `lineID`=? AND `x`=? AND `y`=? AND `z`=? AND `world`=?");

        // ACTIVATOR
        this.addActivator = con.prepareStatement("INSERT INTO `activator` (`lineID`, `waypointID`, `x`, `y`, `z`, `world`, `isBackwards`) VALUES (?, ?, ?, ?, ?, ?, ?)");
        this.deleteActivator = con.prepareStatement("DELETE FROM `activator` WHERE `lineID`=? AND `x`=? AND `y`=? AND `z`=? AND `world`=?");
        this.loadActivatorsForLine = con.prepareStatement("SELECT * FROM `activator` WHERE `lineID`=? ORDER BY `ID` ASC");
        this.deleteActivatorsForLine = con.prepareStatement("DELETE FROM `activator` WHERE `lineID`=?");
        this.getActivatorAtPosition = con.prepareStatement("SELECT * FROM `activator` WHERE `x`=? AND `y`=? AND `z`=? AND `world`=?");
    }

    // ////////////////////////////////////////////////////
    //
    // LINES
    //
    // ////////////////////////////////////////////////////

    public Line addLine(String name, boolean isLoop) {
        try {
            // INSERT INTO `lines` (`name`) VALUES (?)
            this.addLine.setString(1, name);
            int loop = 0;
            if (isLoop) {
                loop = 1;
            }
            this.addLine.setInt(2, loop);
            this.addLine.executeUpdate();
            return this.getLineByName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Line getLineByName(String name) {
        try {
            // SELECT * FROM `lines` WHERE `name`=?
            this.getLineByName.setString(1, name);
            ResultSet results = this.getLineByName.executeQuery();
            while (results != null && results.next()) {
                return new Line(results.getInt("ID"), results.getString("name"), results.getInt("isLoop") == 1);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteLine(Line line) {
        try {
            // DELETE FROM `lines` WHERE `name`=?
            if (this.deleteWaypointsForLine(line) && this.deleteActivatorsForLine(line)) {
                this.deleteLine.setString(1, line.getName());
                this.deleteLine.executeUpdate();
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Line> loadLines() {
        try {
            // SELECT * FROM `lines` ORDER BY `ID` ASC
            ArrayList<Line> list = new ArrayList<Line>();
            ResultSet results = this.loadLines.executeQuery();
            while (results != null && results.next()) {
                Line line = new Line(results.getInt("ID"), results.getString("name"), results.getInt("isLoop") == 1);
                // get waypoints
                Path path = this.loadWaypointsForLine(line.getLineID());
                if (path == null) {
                    continue;
                }
                line.setPath(path);

                // get activators
                HashMap<BlockVector, Activator> activators = this.loadActivatorsForLine(line, path.getWaypoints());
                if (activators == null) {
                    continue;
                }
                line.setActivators(activators);

                // add line
                list.add(line);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ////////////////////////////////////////////////////
    //
    // WAYPOINTS
    //
    // ////////////////////////////////////////////////////

    public Waypoint addWaypoint(Line line, BlockVector vector, float speed) {
        try {
            // INSERT INTO `waypoints` (`lineID`, `x`, `y`, `z`, `world`,
            // `speed`) VALUES (?, ?, ?, ?, ?, ?)
            this.addWaypoint.setInt(1, line.getLineID());
            this.addWaypoint.setInt(2, vector.getX());
            this.addWaypoint.setInt(3, vector.getY());
            this.addWaypoint.setInt(4, vector.getZ());
            this.addWaypoint.setString(5, vector.getWorldName());
            this.addWaypoint.setFloat(6, speed);
            this.addWaypoint.executeUpdate();
            return this.getWaypointAt(line, vector);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Waypoint getWaypointAt(Line line, BlockVector vector) {
        try {
            // SELECT `*` FROM `waypoints` WHERE `lineID`=? AND `x`=? AND `y`=?
            // AND `z`=? AND `world`=?
            this.getWaypointAt.setInt(1, line.getLineID());
            this.getWaypointAt.setInt(2, vector.getX());
            this.getWaypointAt.setInt(3, vector.getY());
            this.getWaypointAt.setInt(4, vector.getZ());
            this.getWaypointAt.setString(5, vector.getWorldName());
            ResultSet results = this.getWaypointAt.executeQuery();
            while (results != null && results.next()) {
                return new Waypoint(results.getInt("ID"), vector, results.getFloat("speed"), line.getPathSize());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean moveWaypoint(Line line, BlockVector oldVector, BlockVector newVector, float speed) {
        try {
            // UPDATE `waypoints` SET `x`=? , `y`=? , `z`=?, `world`=? ,
            // `speed`=? WHERE
            // `lineID`=? AND `x`=? AND `y`=? AND `z`=? AND `world`=?
            this.updateWaypoint.setInt(1, newVector.getX());
            this.updateWaypoint.setInt(2, newVector.getY());
            this.updateWaypoint.setInt(3, newVector.getZ());
            this.updateWaypoint.setString(4, newVector.getWorldName());
            this.updateWaypoint.setFloat(5, speed);
            this.updateWaypoint.setInt(6, line.getLineID());
            this.updateWaypoint.setInt(7, oldVector.getX());
            this.updateWaypoint.setInt(8, oldVector.getY());
            this.updateWaypoint.setInt(9, oldVector.getZ());
            this.updateWaypoint.setString(10, oldVector.getWorldName());
            this.updateWaypoint.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteWaypoint(Line line, BlockVector vector) {
        try {
            // DELETE FROM `waypoints` WHERE `lineID`=? AND `x`=? AND `y`=? AND
            // `z`=? AND `world`=?
            this.deleteWaypoint.setInt(1, line.getLineID());
            this.deleteWaypoint.setInt(2, vector.getX());
            this.deleteWaypoint.setInt(3, vector.getY());
            this.deleteWaypoint.setInt(4, vector.getZ());
            this.deleteWaypoint.setString(5, vector.getWorldName());
            this.deleteWaypoint.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Path loadWaypointsForLine(int lineID) {
        try {
            // SELECT `*` FROM `waypoints` WHERE `lineID`=? ORDER BY `ID` ASC
            Path path = new Path();
            this.loadWaypointsForLine.setInt(1, lineID);
            ResultSet results = this.loadWaypointsForLine.executeQuery();
            int index = 0;
            while (results != null && results.next()) {
                BlockVector vector = new BlockVector(results.getString("world"), results.getInt("x"), results.getInt("y"), results.getInt("z"));
                if (vector.getLocation() == null) {
                    continue;
                }
                Waypoint waypoint = new Waypoint(results.getInt("ID"), vector, results.getFloat("speed"), index);
                ++index;
                path.addWaypoint(waypoint);
            }
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean deleteWaypointsForLine(Line line) {
        try {
            // DELETE FROM `waypoints` WHERE `lineID`=?
            this.deleteWaypointsForLine.setInt(1, line.getLineID());
            this.deleteWaypointsForLine.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ////////////////////////////////////////////////////
    //
    // ACTIVATORS
    //
    // ////////////////////////////////////////////////////

    public Activator createActivator(BlockVector vector, Line line, Waypoint waypoint, boolean isBackwards) {
        try {
            // INSERT INTO `activator` (`lineID`, `waypointID`, `x`, `y`, `z`,
            // `world`, `isBackwards`) VALUES (?, ?, ?, ?, ?, ?, ?)
            this.addActivator.setInt(1, line.getLineID());
            this.addActivator.setInt(2, waypoint.getID());
            this.addActivator.setInt(3, vector.getX());
            this.addActivator.setInt(4, vector.getY());
            this.addActivator.setInt(5, vector.getZ());
            this.addActivator.setString(6, vector.getWorldName());
            int back = 0;
            if (isBackwards) {
                back = 1;
            }
            this.addActivator.setInt(7, back);
            this.addActivator.executeUpdate();
            return this.getActivatorAt(vector, line, waypoint);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private Activator getActivatorAt(BlockVector vector, Line line, Waypoint waypoint) {
        try {
            // SELECT `*` FROM `activator` WHERE `x`=? AND `y`=? AND `z`=? AND
            // `world`=?
            this.getActivatorAtPosition.setInt(1, vector.getX());
            this.getActivatorAtPosition.setInt(2, vector.getY());
            this.getActivatorAtPosition.setInt(3, vector.getZ());
            this.getActivatorAtPosition.setString(4, vector.getWorldName());
            ResultSet results = this.getActivatorAtPosition.executeQuery();
            while (results != null && results.next()) {
                return new Activator(results.getInt("ID"), vector, line, waypoint, results.getInt("isBackwards") == 1);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteActivator(Line line, BlockVector vector) {
        try {
            // DELETE FROM `activator` WHERE `lineID`=? AND `x`=? AND `y`=? AND
            // `z`=? AND `world`=?
            this.deleteActivator.setInt(1, line.getLineID());
            this.deleteActivator.setInt(2, vector.getX());
            this.deleteActivator.setInt(3, vector.getY());
            this.deleteActivator.setInt(4, vector.getZ());
            this.deleteActivator.setString(5, vector.getWorldName());
            this.deleteActivator.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteActivatorsForLine(Line line) {
        try {
            // DELETE FROM `activator` WHERE `lineID`=?
            this.deleteActivatorsForLine.setInt(1, line.getLineID());
            this.deleteActivatorsForLine.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private HashMap<BlockVector, Activator> loadActivatorsForLine(Line line, ArrayList<Waypoint> waypoints) {
        try {
            // SELECT `*` FROM `activator` WHERE `lineID`=? ORDER BY `ID` ASC
            HashMap<BlockVector, Activator> list = new HashMap<BlockVector, Activator>();
            this.loadActivatorsForLine.setInt(1, line.getLineID());
            ResultSet results = this.loadActivatorsForLine.executeQuery();
            while (results != null && results.next()) {
                BlockVector vector = new BlockVector(results.getString("world"), results.getInt("x"), results.getInt("y"), results.getInt("z"));
                if (vector.getLocation() == null) {
                    continue;
                }

                Waypoint waypoint = this.getWaypointByID(waypoints, results.getInt("waypointID"));
                if (waypoint == null) {
                    continue;
                }

                if (vector.getLocation().getBlock().getTypeId() != Material.STONE_BUTTON.getId()) {
                    continue;
                }

                Activator activator = new Activator(results.getInt("ID"), vector, line, waypoint, results.getInt("isBackwards") == 1);
                list.put(vector, activator);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Waypoint getWaypointByID(ArrayList<Waypoint> waypoints, int ID) {
        for (Waypoint waypoint : waypoints) {
            if (waypoint.getID() == ID) {
                return waypoint;
            }
        }
        return null;
    }
}
