package de.minestar.frontschweine.commands;

import org.bukkit.entity.Player;

import de.minestar.frontschweine.core.FrontschweineCore;
import de.minestar.frontschweine.data.BlockVector;
import de.minestar.frontschweine.data.Line;
import de.minestar.minestarlibrary.commands.AbstractExtendedCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class WaypointAddCommand extends AbstractExtendedCommand {

    public WaypointAddCommand(String syntax, String arguments, String node) {
        super(FrontschweineCore.NAME, syntax, arguments, node);
        this.description = "Create a new waypoint";
    }

    public void execute(String[] args, Player player) {
        // LINE MUST BE != NULL
        Line line = FrontschweineCore.lineHandler.getLine(args[0]);
        if (line == null) {
            PlayerUtils.sendError(player, FrontschweineCore.NAME, "Eine Linie mit dem Namen '" + args[0] + "' existiert nicht!");
            return;
        }

        // get speed
        float speed = 0.4f;
        if (args.length > 1) {
            try {
                speed = Float.valueOf(args[1]);
            } catch (Exception e) {
                PlayerUtils.sendError(player, FrontschweineCore.NAME, "Die Geschwindigkeit muss eine Zahl sein (default: 0.4)!");
                return;
            }
        }

        boolean isWaiting = false;
        if (args.length > 2) {
            if (args[2].equalsIgnoreCase("WAIT")) {
                isWaiting = true;
            }
        }

        // add waypoint
        BlockVector vector = new BlockVector(player.getLocation());
        if (FrontschweineCore.lineHandler.addWaypoint(line, vector, speed, isWaiting)) {
            PlayerUtils.sendSuccess(player, FrontschweineCore.NAME, "Wegpunkt wurde hinzugefügt.");
            PlayerUtils.sendInfo(player, "Linie: " + line.getName());
            PlayerUtils.sendInfo(player, "Geschwindigkeit: " + speed);
            return;
        } else {
            PlayerUtils.sendError(player, FrontschweineCore.NAME, "Der Wegpunkt konnte nicht in der Datenbank gespeichert werden!");
            return;
        }
    }
}