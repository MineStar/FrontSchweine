package de.minestar.frontschweine.commands;

import org.bukkit.entity.Player;

import de.minestar.frontschweine.core.FrontschweineCore;
import de.minestar.frontschweine.data.Line;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class LineRemoveCommand extends AbstractCommand {

    public LineRemoveCommand(String syntax, String arguments, String node) {
        super(FrontschweineCore.NAME, syntax, arguments, node);
        this.description = "Delete a line";
    }

    public void execute(String[] args, Player player) {
        // LINE MUST BE != NULL
        Line line = FrontschweineCore.lineHandler.getLine(args[0]);
        if (line == null) {
            PlayerUtils.sendError(player, FrontschweineCore.NAME, "Eine Linie mit dem Namen '" + args[0] + "' existiert nicht!");
            return;
        }

        // delete line
        if (FrontschweineCore.lineHandler.removeLine(line)) {
            PlayerUtils.sendSuccess(player, FrontschweineCore.NAME, "Linie '" + line.getName() + "' wurde gelöscht.");
            return;
        } else {
            PlayerUtils.sendError(player, FrontschweineCore.NAME, "Die Linie '" + line.getName() + "' konnte nicht vollständig gelöscht werden!");
            return;
        }
    }
}