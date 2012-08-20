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

package de.minestar.frontschweine.core;

import java.io.File;

import org.bukkit.plugin.PluginManager;

import de.minestar.frontschweine.handler.DatabaseHandler;
import de.minestar.frontschweine.handler.LineHandler;
import de.minestar.frontschweine.handler.PigHandler;
import de.minestar.frontschweine.listener.ActionListener;
import de.minestar.minestarlibrary.AbstractCore;

public class FrontschweineCore extends AbstractCore {

    public static FrontschweineCore INSTANCE;
    public static final String NAME = "Frontschweine";

    public static ActionListener actionListener;
    public static LineHandler lineHandler;
    public static PigHandler pigHandler;
    public static DatabaseHandler databaseHandler;

    public FrontschweineCore() {
        super(NAME);
        INSTANCE = this;
    }

    @Override
    protected boolean createManager() {
        pigHandler = new PigHandler();
        lineHandler = new LineHandler();
        databaseHandler = new DatabaseHandler(FrontschweineCore.NAME, new File(this.getDataFolder(), "mysql.properties"));

        // INIT : LineHandler
        lineHandler.init();
        return true;
    }

    @Override
    protected boolean createListener() {
        actionListener = new ActionListener(pigHandler);
        return true;
    }

    @Override
    protected boolean registerEvents(PluginManager pm) {
        pm.registerEvents(actionListener, this);
        return true;
    }
}
