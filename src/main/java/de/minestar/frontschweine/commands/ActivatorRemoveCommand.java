package de.minestar.frontschweine.commands;

import org.bukkit.entity.Player;

import de.minestar.frontschweine.core.FrontschweineCore;
import de.minestar.frontschweine.data.PlayerState;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class ActivatorRemoveCommand extends AbstractCommand {

    public ActivatorRemoveCommand(String syntax, String arguments, String node) {
        super(FrontschweineCore.NAME, syntax, arguments, node);
        this.description = "Remove an activator";
    }

    public void execute(String[] args, Player player) {
        FrontschweineCore.playerHandler.getData(player.getName()).update(null, null);
        FrontschweineCore.playerHandler.setState(player.getName(), PlayerState.ACTIVATOR_REMOVE);
        PlayerUtils.sendSuccess(player, FrontschweineCore.NAME, "Klicke auf einen Aktivierer um ihn zu entfernen.");
    }
}