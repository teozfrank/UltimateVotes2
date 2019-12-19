package net.teozfrank.ultimatevotes.commands.vote;

import org.bukkit.command.CommandSender;
import net.teozfrank.ultimatevotes.commands.SubCmd;
import net.teozfrank.ultimatevotes.main.UltimateVotes;

/**
 * Created by frank on 12/07/2014.
 */
public abstract class VoteCmd extends SubCmd {

    public VoteCmd(UltimateVotes plugin, String mainPerm) {
        super(plugin, mainPerm);
    }

    public abstract void run(CommandSender sender, String subCmd, String[] args);
}

