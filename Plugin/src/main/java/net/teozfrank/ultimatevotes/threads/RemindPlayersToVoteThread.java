package net.teozfrank.ultimatevotes.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.*;

import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: Frank
 * Date: 18/08/13
 * Time: 22:49
 * To change this template use File | Settings | File Templates.
 */
public class RemindPlayersToVoteThread implements Runnable {
    private UltimateVotes plugin;

    public RemindPlayersToVoteThread(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        VoteManager vm = plugin.getVoteManager();
        final FileManager fm = plugin.getFileManager();

        if (Bukkit.getOnlinePlayers().size() >= 1) {
            for (final Player player : Util.getOnlinePlayers()) {
                final UUID playerUUID = player.getUniqueId();
                final String playerName = player.getName();
                final String playerWorldName = player.getWorld().getName();

                if (vm.hasNotVotedToday.contains(playerUUID)) {
                    final DatabaseManager databaseManager = plugin.getDatabaseManager();
                    MessageManager mm = plugin.getMessageManager();

                    final List<String> voteReminder = mm.getVoteReminder();
                    final StringBuilder messages = new StringBuilder();
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                        @Override
                        public void run() {
                            if(!plugin.getFileManager().cacheHasVotedOnJoin()) {
                                SendConsoleMessage.warning("Vote reminders disabled due to cache not being populated on join");
                                return;
                            }
                            for(String voteReminderIn: voteReminder) {
                                voteReminderIn = ChatColor.translateAlternateColorCodes('&', voteReminderIn);
                                voteReminderIn = voteReminderIn.replaceAll("%votecount%", String.valueOf(databaseManager.checkUserVotes(playerUUID, "MONTHLYVOTES")));
                                messages.append(voteReminderIn + "\n");
                            }

                            if(!fm.isVoteReminderDisabled(playerWorldName)) {
                                if(!player.hasPermission("ultimatevotes.votereminder.bypass")) {
                                    Util.sendEmptyMsg(player, messages.toString());
                                }

                            }

                        }
                    });

                }

            }
        }

    }
}
