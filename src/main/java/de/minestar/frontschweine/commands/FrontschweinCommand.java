package de.minestar.frontschweine.commands;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.minestar.frontschweine.core.FrontschweineCore;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.commands.AbstractSuperCommand;

public class FrontschweinCommand extends AbstractSuperCommand {

    public FrontschweinCommand(String syntax, String arguments, String node, AbstractCommand... subCommands) {
        super(FrontschweineCore.NAME, syntax, arguments, node, false, subCommands);
    }

    public void execute(String[] args, Player player) {
        // Do nothing
    }

    @Override
    public void execute(String[] args, ConsoleCommandSender console) {
        // Do nothing
    }
}