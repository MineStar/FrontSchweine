package de.minestar.frontschweine.commands;

import org.bukkit.entity.Player;

import de.minestar.frontschweine.core.FrontschweineCore;
import de.minestar.frontschweine.data.BlockVector;
import de.minestar.frontschweine.data.Line;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class WaypointMoveCommand extends AbstractCommand {

    public WaypointMoveCommand(String syntax, String arguments, String node) {
        super(FrontschweineCore.NAME, syntax, arguments, node);
        this.description = "Create a new line";
    }

    public void execute(String[] args, Player player) {
        // LINE MUST BE != NULL
        Line line = FrontschweineCore.lineHandler.getLine(args[0]);
        if (line == null) {
            PlayerUtils.sendError(player, FrontschweineCore.NAME, "Eine Linie mit dem Namen '" + args[0] + "' existiert nicht!");
            return;
        }

        // get speed
        int index = 0;
        try {
            index = Integer.valueOf(args[1]);
        } catch (Exception e) {
            PlayerUtils.sendError(player, FrontschweineCore.NAME, "Der Index muss eine Ganzzahl > 0 sein!");
            return;
        }

        if (index < 1 || index > line.getPathSize()) {
            PlayerUtils.sendError(player, FrontschweineCore.NAME, "Der Index muss kleiner sein als die Anzahl der Wegpunkte.");
            PlayerUtils.sendInfo(player, "Wegpunkte: " + line.getPathSize());
            return;
        }

        // move waypoint
        BlockVector vector = new BlockVector(player.getLocation());
        if (FrontschweineCore.lineHandler.moveWaypoint(line, index - 1, vector)) {
            PlayerUtils.sendSuccess(player, FrontschweineCore.NAME, "Wegpunkt " + index + " wurde verschoben.");
            return;
        } else {
            PlayerUtils.sendError(player, FrontschweineCore.NAME, "Der Wegpunkt konnte nicht verschoben werden!");
            return;
        }
    }
}