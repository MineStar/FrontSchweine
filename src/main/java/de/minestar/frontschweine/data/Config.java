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

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

import de.minestar.frontschweine.core.FrontschweineCore;

public class Config {
    public static BlockVector PIG_VECTOR = null;
    public static int CHECK_RADIUS = 15;

    public static void load() {
        YamlConfiguration configFile = new YamlConfiguration();

        File file = new File(FrontschweineCore.INSTANCE.getDataFolder(), "config.yml");
        if (!file.exists()) {
            generate();
        }

        try {
            configFile.load(file);

            if (!configFile.getString("pig.world").equalsIgnoreCase("NULL")) {
                BlockVector vector = new BlockVector(configFile.getString("pig.world"), configFile.getInt("pig.x"), configFile.getInt("pig.y"), configFile.getInt("pig.z"));
                if (vector.getLocation() != null) {
                    PIG_VECTOR = vector;
                    CHECK_RADIUS = configFile.getInt("check.radius");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generate() {
        try {
            YamlConfiguration configFile = new YamlConfiguration();
            File file = new File(FrontschweineCore.INSTANCE.getDataFolder(), "config.yml");
            configFile.set("pig.world", "NULL");
            configFile.set("pig.x", "0");
            configFile.set("pig.y", "0");
            configFile.set("pig.z", "0");
            configFile.set("check.radius", CHECK_RADIUS);
            configFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            YamlConfiguration configFile = new YamlConfiguration();
            File file = new File(FrontschweineCore.INSTANCE.getDataFolder(), "config.yml");
            configFile.set("pig.world", PIG_VECTOR.getWorldName());
            configFile.set("pig.x", PIG_VECTOR.getX());
            configFile.set("pig.y", PIG_VECTOR.getY());
            configFile.set("pig.z", PIG_VECTOR.getZ());
            configFile.set("check.radius", CHECK_RADIUS);
            configFile.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
