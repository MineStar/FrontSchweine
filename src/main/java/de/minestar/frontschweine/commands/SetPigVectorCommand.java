package de.minestar.frontschweine.commands;

import org.bukkit.entity.Player;

import de.minestar.frontschweine.core.FrontschweineCore;
import de.minestar.frontschweine.data.BlockVector;
import de.minestar.frontschweine.data.Config;
import de.minestar.minestarlibrary.commands.AbstractCommand;
import de.minestar.minestarlibrary.utils.PlayerUtils;

public class SetPigVectorCommand extends AbstractCommand {

    public SetPigVectorCommand(String syntax, String arguments, String node) {
        super(FrontschweineCore.NAME, syntax, arguments, node);
        this.description = "Set the pigvector";
    }

    public void execute(String[] args, Player player) {
        Config.PIG_VECTOR = new BlockVector(player.getLocation());
        Config.save();
        PlayerUtils.sendSuccess(player, FrontschweineCore.NAME, "Schweinevector wurde gesetzt.");
    }
}