package net.teozfrank.ultimatevotes.commands.vote;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.Util;

/**
 * Created by frank on 12/07/2014.
 */
public class AboutCmd extends VoteCmd {

    public AboutCmd(UltimateVotes plugin, String mainPerm) {
        super(plugin, mainPerm);
    }

    @Override
    public void run(CommandSender sender, String subCmd, String[] args) {
        Util.sendEmptyMsg(sender,Util.LINE_BREAK);
        Util.sendEmptyMsg(sender,"");
        Util.sendEmptyMsg(sender, ChatColor.translateAlternateColorCodes('&', "&aUltimateVotes &bV" + UltimateVotes.getPluginVersion()));
        Util.sendEmptyMsg(sender, ChatColor.translateAlternateColorCodes('&', "&aby &bTeOzFrank"));
        Util.sendEmptyMsg(sender,"");
        Util.sendEmptyMsg(sender,Util.LINE_BREAK);
    }
}
