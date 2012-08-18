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
        this.deleteWaypoint = con.prepareStatement("DELETE FROM `waypoints` WHERE `x`=? AND `y`=? AND `z`=? AND `world`=?");
        this.loadWaypoints = con.prepareStatement("SELECT * FROM `waypoints` ORDER BY `ID` ASC");
        this.deleteWaypointsForLine = con.prepareStatement("DELETE FROM `waypoints` WHERE `lineID`=?");

        // ACTIVATOR
        this.addActivator = con.prepareStatement("INSERT INTO `activator` (`lineID`, `x`, `y`, `z`, `world`) VALUES (?, ?, ?, ?, ?)");
        this.loadActivators = con.prepareStatement("DELETE FROM `activator` WHERE `x`=? AND `y`=? AND `z`=? AND `world`=?");
        this.deleteActivator = con.prepareStatement("SELECT * FROM `activator` ORDER BY `ID` ASC");
        this.deleteActivatorForLine = con.prepareStatement("DELETE FROM `activator` WHERE `lineID`=?");
    }
}
