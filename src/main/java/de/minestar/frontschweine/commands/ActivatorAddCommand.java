package de.minestar.frontschweine.commands;

import org.bukkit.entity.Player;

import de.minestar.frontschweine.core.FrontschweineCore;
import de.minestar.frontschweine.data.Line;
import de.minestar.frontschweine.data.PlayerState;
import de.minestar.frontschweine.data.Waypoint;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class ActivatorAddCommand extends AbstractCommand {

    public ActivatorAddCommand(String syntax, String arguments, String node) {
        super(FrontschweineCore.NAME, syntax, arguments, node);
        this.description = "Create an activator";
    }

    public void execute(String[] args, Player player) {
        // LINE MUST BE != NULL
        Line line = FrontschweineCore.lineHandler.getLine(args[0]);
        if (line == null) {
            PlayerUtils.sendError(player, FrontschweineCore.NAME, "Eine Linie mit dem Namen '" + args[0] + "' existiert nicht!");
            return;
        }

        // get index
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

        // get the waypoint
        Waypoint waypoint = line.getWaypoint(index - 1);

        FrontschweineCore.playerHandler.getData(player.getName()).update(line, waypoint);
        FrontschweineCore.playerHandler.setState(player.getName(), PlayerState.ACTIVATOR_ADD);
        PlayerUtils.sendSuccess(player, FrontschweineCore.NAME, "Klicke auf einen Stone-Button um einen Aktivierer zu erstellen.");
    }
}