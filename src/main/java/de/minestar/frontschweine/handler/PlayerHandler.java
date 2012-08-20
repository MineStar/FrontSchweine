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

import java.util.HashMap;

import de.minestar.frontschweine.data.PlayerData;
import de.minestar.frontschweine.data.PlayerState;

public class PlayerHandler {
    private HashMap<String, PlayerData> playerList;

    public PlayerHandler() {
        this.playerList = new HashMap<String, PlayerData>(64);
    }

    public PlayerData getData(String playerName) {
        PlayerData data = this.playerList.get(playerName.toLowerCase());
        if (data == null) {
            data = new PlayerData(playerName);
            this.playerList.put(playerName.toLowerCase(), data);
        }
        return data;
    }

    public void setState(String playerName, PlayerState state) {
        this.getData(playerName).setState(state);
    }

    public PlayerState getState(String playerName) {
        return this.getData(playerName).getState();
    }
}
