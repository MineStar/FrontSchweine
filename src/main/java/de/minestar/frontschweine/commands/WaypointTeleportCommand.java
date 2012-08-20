package de.minestar.frontschweine.commands;

import org.bukkit.entity.Player;

import de.minestar.frontschweine.core.FrontschweineCore;
import de.minestar.frontschweine.data.Line;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class WaypointTeleportCommand extends AbstractCommand {

    public WaypointTeleportCommand(String syntax, String arguments, String node) {
        super(FrontschweineCore.NAME, syntax, arguments, node);
        this.description = "Remove a waypoint";
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

        // teleport player to waypoint
        player.teleport(line.getWaypoint(index - 1).getLocation());
        PlayerUtils.sendSuccess(player, FrontschweineCore.NAME, "Du wurdest zu Wegpunkt " + index + " teleportiert.");
    }
}