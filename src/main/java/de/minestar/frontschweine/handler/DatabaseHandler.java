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

import de.minestar.frontschweine.data.Waypoint;
import de.minestar.minestarlibrary.database.AbstractMySQLHandler;
import de.minestar.minestarlibrary.database.DatabaseUtils;

public class DatabaseHandler extends AbstractMySQLHandler {

    PreparedStatement addLine, loadLines, deleteLine;
    PreparedStatement addWaypoint, loadWaypoints, deleteWaypoint, deleteWaypointsForLine;
    PreparedStatement addActivator, loadActivators, deleteActivator, deleteActivatorForLine;

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
        this.addLine = con.prepareStatement("INSERT INTO `lines` (`name`) VALUES (?)");
        this.deleteLine = con.prepareStatement("DELETE FROM `lines` WHERE `name`=?");
        this.loadLines = con.prepareStatement("SELECT * FROM `lines` ORDER BY `ID` ASC");

        // WAYPOINTS
        this.addWaypoint = con.prepareStatement("INSERT INTO `waypoints` (`lineID`, `x`, `y`, `z`, `world`, `speed`) VALUES (?, ?, ?, ?, ?, ?)");
        this.deleteWaypoint = con.prepareStatement("DELETE FROM `waypoints` WHERE `lineID`=? AND `x`=? AND `y`=? AND `z`=? AND `world`=?");
        this.loadWaypoints = con.prepareStatement("SELECT `*` FROM `waypoints` WHERE lineID`=? ORDER BY `ID` ASC");
        this.deleteWaypointsForLine = con.prepareStatement("DELETE FROM `waypoints` WHERE `lineID`=?");

        // ACTIVATOR
        this.addActivator = con.prepareStatement("INSERT INTO `activator` (`lineID`, `x`, `y`, `z`, `world`) VALUES (?, ?, ?, ?, ?)");
        this.deleteActivator = con.prepareStatement("DELETE FROM `activator` WHERE `lineID`=? AND `x`=? AND `y`=? AND `z`=? AND `world`=?");
        this.loadActivators = con.prepareStatement("SELECT `*` FROM `activator`  WHERE lineID`=? ORDER BY `ID` ASC");
        this.deleteActivatorForLine = con.prepareStatement("DELETE FROM `activator` WHERE `lineID`=?");
    }

    // ////////////////////////////////////////////////////
    //
    // LINES
    //
    // ////////////////////////////////////////////////////

    public boolean addLine(String name) {
        try {
            this.addLine.setString(1, name);
            this.addLine.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteLine(int lineID, String name) {
        try {
            if (this.deleteWaypointsForLine(lineID) && this.deleteActivatorsForLine(lineID)) {
                this.deleteLine.setString(1, name);
                this.deleteLine.executeUpdate();
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // ////////////////////////////////////////////////////
    //
    // WAYPOINTS
    //
    // ////////////////////////////////////////////////////

    public boolean addWaypoint(int lineID, Waypoint waypoint) {
        try {
            this.addWaypoint.setInt(1, lineID);
            this.addWaypoint.setInt(2, waypoint.getX());
            this.addWaypoint.setInt(3, waypoint.getY());
            this.addWaypoint.setInt(4, waypoint.getZ());
            this.addWaypoint.setString(5, waypoint.getWorldName());
            this.addWaypoint.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteWaypoint(int lineID, Waypoint waypoint) {
        try {
            this.deleteWaypoint.setInt(1, lineID);
            this.deleteWaypoint.setInt(2, waypoint.getX());
            this.deleteWaypoint.setInt(3, waypoint.getY());
            this.deleteWaypoint.setInt(4, waypoint.getZ());
            this.deleteWaypoint.setString(5, waypoint.getWorldName());
            this.deleteWaypoint.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean deleteWaypointsForLine(int lineID) {
        try {
            this.deleteWaypointsForLine.setInt(1, lineID);
            this.deleteWaypointsForLine.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ////////////////////////////////////////////////////
    //
    // ACTIVATORS
    //
    // ////////////////////////////////////////////////////

    public boolean addActivator(int lineID, Waypoint waypoint) {
        try {
            this.addActivator.setInt(1, lineID);
            this.addActivator.setInt(2, waypoint.getX());
            this.addActivator.setInt(3, waypoint.getY());
            this.addActivator.setInt(4, waypoint.getZ());
            this.addActivator.setString(5, waypoint.getWorldName());
            this.addActivator.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteActivator(int lineID, Waypoint waypoint) {
        try {
            this.deleteActivator.setInt(1, lineID);
            this.deleteActivator.setInt(2, waypoint.getX());
            this.deleteActivator.setInt(3, waypoint.getY());
            this.deleteActivator.setInt(4, waypoint.getZ());
            this.deleteActivator.setString(5, waypoint.getWorldName());
            this.deleteActivator.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean deleteActivatorsForLine(int lineID) {
        try {
            this.deleteActivatorForLine.setInt(1, lineID);
            this.deleteActivatorForLine.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
