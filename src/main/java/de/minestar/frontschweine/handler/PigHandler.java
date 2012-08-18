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
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import de.minestar.frontschweine.data.PigData;

public class PigHandler {
    public HashMap<String, PigData> pigByPlayerMap = new HashMap<String, PigData>();
    public HashMap<UUID, PigData> pigByUUIDMap = new HashMap<UUID, PigData>();

    public void addPigData(PigData pigData) {
        this.pigByPlayerMap.put(pigData.getPlayerName(), pigData);
        this.pigByUUIDMap.put(pigData.getUUID(), pigData);
    }

    public void removePig(PigData pigData) {
        this.pigByPlayerMap.remove(pigData.getPlayerName());
        this.pigByUUIDMap.remove(pigData.getUUID());
    }

    // ////////////////////////////////////////////////////////
    //
    // PigData by Player
    //
    // ////////////////////////////////////////////////////////

    public boolean hasPigDataByPlayer(Player player) {
        return this.pigByPlayerMap.containsKey(player.getName());
    }

    public boolean hasPigDataByPlayer(String playerName) {
        return this.pigByPlayerMap.containsKey(playerName);
    }

    public PigData getPigDataByPlayer(Player player) {
        return this.getPigDataByPlayer(player.getName());
    }

    public PigData getPigDataByPlayer(String playerName) {
        return this.pigByPlayerMap.get(playerName);
    }

    // ////////////////////////////////////////////////////////
    //
    // PigData by UUID
    //
    // ////////////////////////////////////////////////////////

    public PigData getPigDataByUUID(Entity entity) {
        return this.pigByUUIDMap.get(entity.getUniqueId());
    }

    public PigData getPigDataByUUID(UUID uuid) {
        return this.pigByUUIDMap.get(uuid);
    }

    public boolean hasPigDataByUUID(Entity entity) {
        return this.pigByUUIDMap.containsKey(entity.getUniqueId());
    }

    public boolean hasPigDataByUUID(UUID uuid) {
        return this.pigByUUIDMap.containsKey(uuid);
    }
}
