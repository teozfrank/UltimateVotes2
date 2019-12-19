package net.teozfrank.ultimatevotes.commands.vote;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.MessageManager;
import net.teozfrank.ultimatevotes.util.Util;

import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Created by frank on 12/07/2014.
 */
public class AllTimeCmd extends VoteCmd {

    public AllTimeCmd(UltimateVotes plugin, String mainPerm) {
        super(plugin, mainPerm);
    }

    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss zzzz Z");

    @Override
    public void run(CommandSender sender, String subCmd, String[] args) {

        MessageManager mm = plugin.getMessageManager();

        if(!(sender instanceof Player)) {
            Util.sendMsg(sender, NO_CONSOLE);
            return;
        }

        if (!(plugin.getVoteManager().allVotes.size() < 1)) {
            Util.cmdTitle(sender, mm.getVoteAllTimeTitle());
            Util.sendEmptyMsg(sender, "");
            int value = 1;
            for (Map.Entry<String, Integer> values : plugin.getVoteManager().allVotes.entrySet()) {
                String topListFormat = mm.getTopListFormatMessage();
                topListFormat = topListFormat.replaceAll("%position%", String.valueOf(value));
                topListFormat = topListFormat.replaceAll("%player%", values.getKey());
                topListFormat = topListFormat.replaceAll("%votecount%", String.valueOf(values.getValue()));
                Util.sendEmptyMsg(sender, topListFormat);
                value++;
            }
            Util.sendEmptyMsg(sender, "");
            Util.sendEmptyMsg(sender, mm.getLastUpdatedFormatted(plugin.getLastUpdatedMinsFormatted(), plugin.getLastUpdatedSecondsFormatted()) );
            Util.sendEmptyMsg(sender, "");
            Util.sendPluginCredits(sender);
            return;
        } else {
            Util.sendMsg(sender, ChatColor.YELLOW + "Votes have not been loaded! or no Votes exist to load!");
            return;
        }

    }
}
