package net.teozfrank.ultimatevotes.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.*;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Frank
 * Date: 18/08/13
 * Time: 22:31
 * To change this template use File | Settings | File Templates.
 */
public class PlayerJoin implements Listener {

    private UltimateVotes plugin;

    public PlayerJoin(UltimateVotes plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent e) {
        final VoteManager vm = plugin.getVoteManager();
        final DatabaseManager databaseManager = plugin.getDatabaseManager();
        final RewardsManager rm = plugin.getRewardsManager();
        final FileManager fm = plugin.getFileManager();
        final MessageManager mm = plugin.getMessageManager();

        final Player player = e.getPlayer();
        final String playerName = player.getName();
        final UUID playerUUID = player.getUniqueId();

        if(plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Player " + playerName + "has joined the server with uuid: " + playerUUID);
        }

        if (! databaseManager.getCachedUUIDs().containsValue(playerUUID)) {
            databaseManager.getCachedUUIDs().put(playerName, playerUUID);
        }

        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

            @Override
            public void run() {
                if (rm.hasUnclaimedRewards(playerUUID) && !fm.useClaimCommand() && fm.isRewardsEnabled()) { //if the player has offline rewards and we are not using the claim command
                    rm.rewardPlayer(player);
                }
                if (fm.isUpdateCheckEnabled() && player.hasPermission("ultimatevotes.admin.updatenotification")) {

                    if(plugin.getDescription().getVersion().contains("dev")) {
                        Util.sendMsg(player, ChatColor.GOLD + "Update checking is disabled for dev versions.");
                        return;
                    }
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    final String version = Util.getSpigotVersion();

                                    if(version == null) {
                                        SendConsoleMessage.error("Could not check for updates!");
                                        return;

                                    }

                                    if (! version.equals(plugin.getDescription().getVersion())) {
                                        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                                            @Override
                                            public void run() {
                                                Util.sendEmptyMsg(player, ChatColor.translateAlternateColorCodes('&', "&b&l---------------------------------"));
                                                Util.sendEmptyMsg(player, ChatColor.translateAlternateColorCodes('&', ""));
                                                Util.sendEmptyMsg(player, ChatColor.translateAlternateColorCodes('&', "&6&lThere is an update for UltimateVotes!"));
                                                Util.sendEmptyMsg(player, ChatColor.translateAlternateColorCodes('&', "&6&lPlease update me!"));
                                                Util.sendEmptyMsg(player, ChatColor.translateAlternateColorCodes('&', ""));
                                                Util.sendEmptyMsg(player, ChatColor.translateAlternateColorCodes('&', "&b&l---------------------------------"));
                                            }
                                        });

                                    }
                                }
                            });

                        }
                    }, 40L);
                }
            }


        }, 20L);

        if (vm.hasVotedToday.containsKey(playerUUID)) {
            Date playerVoteDateIn = vm.hasVotedToday.get(playerUUID);

            Long playerVoteDate = playerVoteDateIn.getTime();
            Long currentTime = System.currentTimeMillis();
            Long difference = currentTime - playerVoteDate;
            Long days = TimeUnit.MILLISECONDS.toDays(difference);

            if(plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Playervote date: " + playerVoteDate);
                SendConsoleMessage.debug("current date: " + currentTime);
                SendConsoleMessage.debug("difference: " + difference);
                SendConsoleMessage.debug("days: " + days);
            }
            if(days >= 1) {
                vm.hasVotedToday.remove(playerUUID);
            } else {
                return;
            }

        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                if (!databaseManager.hasVotedToday(playerUUID)) {
                    vm.hasNotVotedToday.add(playerUUID);
                    if (plugin.isDebugEnabled()) {
                        SendConsoleMessage.debug("Not voted");
                    }
                    if (fm.isJoinMessagesEnabled()) {
                        final String voteCount = String.valueOf(databaseManager.checkUserVotes(playerUUID, "MONTHLYVOTES"));
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                            @Override
                            public void run() {
                                List<String> joinMessages = mm.getJoinMessage();
                                StringBuilder messages = new StringBuilder();

                                for (String joinMessage : joinMessages) {
                                    joinMessage = ChatColor.translateAlternateColorCodes('&', joinMessage);
                                    joinMessage = joinMessage.replaceAll("%votecount%", voteCount);
                                    messages.append(joinMessage + "\n");
                                }

                                Util.sendEmptyMsg(player, messages.toString());
                            }
                        }, 60L);
                    }
                } else {
                    if (vm.hasNotVotedToday.contains(playerUUID)) {
                        if (plugin.isDebugEnabled()) {
                            SendConsoleMessage.debug("removed from not voted today");
                        }
                        vm.hasNotVotedToday.remove(playerUUID);
                    }
                    vm.hasVotedToday.put(playerUUID, new java.sql.Date(System.currentTimeMillis()));
                    if (plugin.isDebugEnabled()) {
                        SendConsoleMessage.debug("Has voted");
                    }
                }
            }
        });


    }
}
