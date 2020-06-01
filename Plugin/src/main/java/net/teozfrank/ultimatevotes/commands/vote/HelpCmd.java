package net.teozfrank.ultimatevotes.commands.vote;

import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.Util;
import org.bukkit.command.CommandSender;

public class HelpCmd extends VoteCmd {

    public HelpCmd(UltimateVotes plugin, String mainPerm) {
        super(plugin, mainPerm);
    }

    @Override
    public void run(CommandSender sender, String subCmd, String[] args) {
        Util.sendEmptyMsg(sender,Util.LINE_BREAK);
        Util.sendEmptyMsg(sender,"");
        Util.sendEmptyMsg(sender, plugin.getMessageManager().getVoteCommandListTitle());
        Util.sendEmptyMsg(sender,"");
        Util.printList(sender, plugin.getMessageManager().getVoteCommandList());
        Util.sendEmptyMsg(sender,"");
        Util.sendPluginCredits(sender);
    }
}
