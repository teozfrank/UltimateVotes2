package net.teozfrank.ultimatevotes.events;

import net.teozfrank.ultimatevotes.discord.DiscordFileManager;
import net.teozfrank.ultimatevotes.discord.DiscordWebhookManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.*;

import java.util.UUID;

public class PlayerVote implements Listener {

    UltimateVotes plugin;

    public PlayerVote(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerVoteMade(VotifierEvent e) {

        final VoteManager vm = plugin.getVoteManager();
        final DatabaseManager databaseManager = plugin.getDatabaseManager();
        final RewardsManager rm = plugin.getRewardsManager();
        final FileManager fm = plugin.getFileManager();
        final MessageManager mm = plugin.getMessageManager();
        final DiscordWebhookManager dwm = plugin.getDiscordWebhookManager();
        final DiscordFileManager dfm = plugin.getDiscordFileManager();

        final Vote v = e.getVote();
        String serviceName = v.getServiceName();
        String ipAddress = v.getAddress();


        final String username = v.getUsername();

        if(!fm.isMySqlEnabled()) {
            return;
        }

        if(plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Vote received from username " + username + " from " + v.getServiceName() + " with IP " + v.getAddress());
        }

        if(v.getUsername().length() == 0 || v.getUsername().equalsIgnoreCase("anonymous")) {
            return;//ignored
        }

        if (!(v.getUsername().equals(""))) {//to prevent votes that put in no username being counted
            if(plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("logging vote for player: " + v.getUsername());
            }
            plugin.getServer().getScheduler().runTask(plugin, new Runnable() {

                @Override
                public void run() {
                    try {

                        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                            @Override
                            public void run() {
                                if(plugin.isDebugEnabled()) {
                                    SendConsoleMessage.debug("Async task retrieve uuid from username");
                                }
                                final UUID playerUUID = databaseManager.getUUIDFromUsername(username);

                                if(playerUUID == null) {
                                    if(plugin.isDebugEnabled()) {
                                        SendConsoleMessage.debug("Player UUID is null.");
                                    }
                                    return;// dont continue
                                }

                                databaseManager.addPlayerMonthlyVote(playerUUID, username);
                                databaseManager.addPlayerAllTimeVote(playerUUID, username);
                                databaseManager.addVoteLog(playerUUID, username, serviceName, ipAddress);

                                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        if(plugin.isDebugEnabled()) {
                                            SendConsoleMessage.debug("Player UUID for player " + username + " : " + playerUUID);
                                        }

                                        vm.handleNotVoted(playerUUID);
                                        Player player = Bukkit.getPlayer(username);

                                        if(fm.isVoteBroadcastEnabled() && !fm.isVoteBroadcastOnlineEnabled()) {
                                            String voteAnnouncement = mm.getVoteBroadcastMessage();
                                            voteAnnouncement = voteAnnouncement.replaceAll("%player%", username);
                                            voteAnnouncement = voteAnnouncement.replaceAll("%service%", v.getServiceName());
                                            Util.broadcast(voteAnnouncement);
                                        }

                                        /*if(dfm.isVoteWebhookEventEnabled()) {
                                            dwm.sendVoteNotification(username, v.getServiceName(), v.getAddress());
                                        }*/

                                        if(player != null) {//if the player is online
                                            if(plugin.isDebugEnabled()) {
                                                SendConsoleMessage.debug("Player " + player.getName() + " is online rewarding them.");
                                            }

                                            if(rm.hasUnclaimedRewards(playerUUID) && fm.isRewardsEnabled()) {//if the player has unclaimed rewards and rewards are enabled
                                                if(plugin.isDebugEnabled()) {
                                                    SendConsoleMessage.debug("Player has unclaimed votes and rewards are enabled.");
                                                }

                                                if(fm.isVoteBroadcastEnabled() && fm.isVoteBroadcastOnlineEnabled()) {
                                                    String voteAnnouncement = mm.getVoteBroadcastMessage();
                                                    voteAnnouncement = voteAnnouncement.replaceAll("%player%", username);
                                                    voteAnnouncement = voteAnnouncement.replaceAll("%service%", v.getServiceName());
                                                    Util.broadcast(voteAnnouncement);
                                                }


                                                if(!fm.rewardOnline() && fm.useClaimCommand()) {
                                                    if(plugin.isDebugEnabled()) {
                                                        SendConsoleMessage.debug("do not reward player as claiming is enabled and reward online is off");
                                                    }
                                                    return;
                                                }

                                                rm.rewardPlayer(player);
                                            }


                                        }
                                    }
                                });

                            }
                        });

                    } catch (NullPointerException e) {
                        if(plugin.isDebugEnabled()) {
                            SendConsoleMessage.debug("Player uuid is null, ignoring.");
                        }
                    }

                }
            });

        } else {
            if(plugin.isDebugEnabled()) { SendConsoleMessage.debug("Vote ignored as no valid username was entered."); }
        }

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (plugin.isDebugEnabled() && p.isOp()) {
                p.sendMessage(ChatColor.GOLD + "This is as debug message from Ultimate Votes!");
                p.sendMessage(ChatColor.GREEN + v.getUsername() + " voted for the server from: " + v.getAddress() + " at: " + v.getTimeStamp());
            }
        }
    }
}
