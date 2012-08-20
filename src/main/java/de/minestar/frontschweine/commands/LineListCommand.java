package de.minestar.frontschweine.commands;

import java.util.Map;
import java.util.TreeMap;

import org.bukkit.entity.Player;

import de.minestar.frontschweine.core.FrontschweineCore;
import de.minestar.frontschweine.data.Line;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class LineListCommand extends AbstractCommand {

    public LineListCommand(String syntax, String arguments, String node) {
        super(FrontschweineCore.NAME, syntax, arguments, node);
        this.description = "Create a new line";
    }

    public void execute(String[] args, Player player) {
        TreeMap<String, Line> list = FrontschweineCore.lineHandler.getAllLines();
        PlayerUtils.sendInfo(player, "---------------------------------------------");
        PlayerUtils.sendInfo(player, FrontschweineCore.NAME, "Registrierte Linien:");
        PlayerUtils.sendInfo(player, "---------------------------------------------");
        for (Map.Entry<String, Line> entry : list.entrySet()) {
            PlayerUtils.sendInfo(player, entry.getValue().getName() + " ( ID: " + entry.getValue().getLineID() + " )");
        }
        PlayerUtils.sendInfo(player, "---------------------------------------------");
    }
}