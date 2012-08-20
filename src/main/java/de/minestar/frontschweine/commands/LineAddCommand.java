package de.minestar.frontschweine.commands;

import org.bukkit.entity.Player;

import de.minestar.frontschweine.core.FrontschweineCore;
import de.minestar.frontschweine.data.Line;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class LineAddCommand extends AbstractCommand {

    public LineAddCommand(String syntax, String arguments, String node) {
        super(FrontschweineCore.NAME, syntax, arguments, node);
        this.description = "Create a new line";
    }

    public void execute(String[] args, Player player) {
        // LINE MUST BE == NULL
        Line line = FrontschweineCore.lineHandler.getLine(args[0]);
        if (line != null) {
            PlayerUtils.sendError(player, FrontschweineCore.NAME, "Eine Linie mit dem Namen '" + line.getName() + "' existiert bereits!");
            return;
        }

        boolean isLoop = args[1].equalsIgnoreCase("true");

        // create line
        if (FrontschweineCore.lineHandler.addLine(args[0], isLoop)) {
            PlayerUtils.sendSuccess(player, FrontschweineCore.NAME, "Linie '" + args[0] + "' wurde hinzugefügt.");
            return;
        } else {
            PlayerUtils.sendError(player, FrontschweineCore.NAME, "Die Linie '" + args[0] + "' konnte nicht in der Datenbank gespeichert werden!");
            return;
        }
    }
}