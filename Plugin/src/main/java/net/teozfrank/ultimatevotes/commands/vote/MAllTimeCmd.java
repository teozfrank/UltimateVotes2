package net.teozfrank.ultimatevotes.commands.vote;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.MessageManager;
import net.teozfrank.ultimatevotes.util.Util;

import java.util.UUID;

/**
 * Created by frank on 12/07/2014.
 */
public class MAllTimeCmd extends VoteCmd {

    public MAllTimeCmd(UltimateVotes plugin, String mainPerm) {
        super(plugin, mainPerm);
    }

    @Override
    public void run(final CommandSender sender, String subCmd, String[] args) {

        final MessageManager mm = plugin.getMessageManager();
        if(!(sender instanceof Player)) {
            Util.sendMsg(sender, NO_CONSOLE);
            return;
        }

        final Player player = (Player) sender;
        final UUID playerUUID = player.getUniqueId();
        final String tableName = "ALLVOTES";
        final String title = mm.getMVoteAllTimeTitle();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                int playerVotes = plugin.getDatabaseManager().checkUserVotes(playerUUID, tableName);

                if (playerVotes == 0) {
                    Util.cmdTitle(sender, title);
                    Util.sendVoteCountMessage(sender, ChatColor.YELLOW + "You do not have any votes yet please use /vote to vote.");
                    Util.sendPluginCredits(sender);
                    return;
                } else if (playerVotes > 0) {
                    Util.cmdTitle(sender, title);
                    String mAllTimeMessage = mm.getMAllTimeCmdMessage();
                    mAllTimeMessage = mAllTimeMessage.replaceAll("%votecount%", String.valueOf(playerVotes));
                    Util.sendVoteCountMessage(sender, ChatColor.translateAlternateColorCodes('&' , mAllTimeMessage));
                    Util.sendPluginCredits(sender);
                    return;
                } else {
                    Util.sendMsg(sender, ChatColor.RED + "There was an error trying to retrieve your votes, please notify an admin!");
                    return;
                }
            }
        });



    }
}
