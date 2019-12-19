package net.teozfrank.ultimatevotes.commands.vote;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.DatabaseManager;
import net.teozfrank.ultimatevotes.util.MessageManager;
import net.teozfrank.ultimatevotes.util.Util;

import java.util.List;
import java.util.UUID;

/**
 * Created by frank on 12/07/2014.
 */
public class SitesCmd extends VoteCmd {

    public SitesCmd(UltimateVotes plugin, String mainPerm) {
        super(plugin, mainPerm);
    }

    @Override
    public void run(final CommandSender sender, String subCmd, String[] args) {

        if(!(sender instanceof Player)) {
            Util.sendMsg(sender, NO_CONSOLE);
            return;
        }
        final DatabaseManager databaseManager = plugin.getDatabaseManager();
        MessageManager mm = plugin.getMessageManager();
        Player player = (Player) sender;
        final String playerName = player.getName();
        final UUID playerUUID = player.getUniqueId();

        final List<String> voteSites = mm.getVoteSites();
        final StringBuilder messages = new StringBuilder();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                for(String voteSite: voteSites) {
                    voteSite = ChatColor.translateAlternateColorCodes('&', voteSite);
                    voteSite = voteSite.replaceAll("%votecount%", String.valueOf(databaseManager.checkUserVotes(playerUUID, "MONTHLYVOTES")));

                    messages.append(voteSite + "\n");
                }
                Util.sendEmptyMsg(sender, messages.toString());
            }
        });


    }
}
