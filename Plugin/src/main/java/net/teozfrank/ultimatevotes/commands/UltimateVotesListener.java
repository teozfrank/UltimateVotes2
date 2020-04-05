package net.teozfrank.ultimatevotes.commands;

import net.teozfrank.ultimatevotes.api.WorldEditSelectionHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.*;
import util.WorldEditSelection;

import java.util.UUID;

public class UltimateVotesListener implements CommandExecutor {

    private UltimateVotes plugin;

    public UltimateVotesListener(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args) {
        final DatabaseManager databaseManager = plugin.getDatabaseManager();

        if (args.length < 1) {
            Util.cmdTitle(sender, "                    UltimateVotes Admin commands");
            Util.sendEmptyMsg(sender, "");
            Util.sendEmptyMsg(sender, ChatColor.GOLD + "/uv reload " + ChatColor.GREEN + "- reload the plugin config from disk");
            Util.sendEmptyMsg(sender, ChatColor.GOLD + "/uv addtestvote <player>" + ChatColor.GREEN + "- simulate a player voting, this does not reward the player.");
            Util.sendEmptyMsg(sender, ChatColor.GOLD + "/uv rewardplayer <player>" + ChatColor.GREEN + "- attempt to reward a player if they have unclaimed votes.");
            Util.sendEmptyMsg(sender, ChatColor.GOLD + "/uv broadcast <message> " + ChatColor.GREEN + "- broadcast a message, you can use this to send messages for rewards.");
            Util.sendEmptyMsg(sender, ChatColor.GOLD + "/uv setwallsigns " + ChatColor.GREEN + "- set the wall of signs using a worldedit region selection.");
            Util.sendEmptyMsg(sender, ChatColor.GOLD + "/uv checktimedcmds " + ChatColor.GREEN + "- check the timed cmds and run any that have expired.");
            Util.sendEmptyMsg(sender, ChatColor.GOLD + "/uv restart " + ChatColor.GREEN + "- restart the plugin");
            Util.sendEmptyMsg(sender, ChatColor.GOLD + "/uv checkmonthly <playername> " + ChatColor.GREEN + "- check monthly votes for a given player.");
            Util.sendEmptyMsg(sender, ChatColor.GOLD + "/uv checkalltime <playername> " + ChatColor.GREEN + "- check alltime votes for a given player.");
            Util.sendEmptyMsg(sender, ChatColor.GOLD + "/uv sendvotewebhook <playername> <service> " + ChatColor.GREEN + "- send a test discord notice to a configured webhook.");
            Util.sendEmptyMsg(sender, ChatColor.YELLOW + "------RESET COMMANDS - BE CAREFUL USING THESE!-------");
            Util.sendEmptyMsg(sender, ChatColor.GOLD + "/uv resetmonthly " + ChatColor.GREEN + "- resets the monthly votes - " + ChatColor.RED + "WARNING THIS PROCESS CANNOT BE UNDONE!\n\n");
            Util.sendEmptyMsg(sender, "");
            Util.sendPluginCredits(sender);

            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            Util.sendMsg(sender, ChatColor.GOLD + "reloading plugin yml's.");
            plugin.reloadConfig();
            Util.sendMsg(sender, ChatColor.GOLD + "config.yml reloaded from disk.");
            plugin.getFileManager().reloadRewards();
            Util.sendMsg(sender, ChatColor.GOLD + "rewards.yml reloaded from disk.");
            plugin.getFileManager().reloadMessages();
            Util.sendMsg(sender, ChatColor.GOLD + "messages.yml reloaded from disk.");
            plugin.getFileManager().reloadDiscord();
            Util.sendMsg(sender, ChatColor.GOLD + "discord.yml reloaded from disk.");
            Util.sendMsg(sender, ChatColor.GOLD + "reload complete!");
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("restart")) {
            Bukkit.getPluginManager().disablePlugin(plugin);
            Bukkit.getPluginManager().enablePlugin(plugin);
            Util.sendMsg(sender, ChatColor.GOLD + "Plugin restarted successfully!");
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("resetmonthly")) {

            plugin.getDatabaseManager().resetVotes("MONTHLYVOTES");
            Util.sendMsg(sender, ChatColor.GREEN + "Monthly votes have been reset!");

            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("setwallsigns")) {
            if (sender instanceof Player) {

                Player player = (Player) sender;
                FileManager fm = plugin.getFileManager();
                SignManager sm = plugin.getSignManager();
                WorldEditSelectionHelper wesh = plugin.getWorldEditSelectionHelper();

                WorldEditSelection selection = wesh.getWorldEditSelection(player);

                if(plugin.isDebugEnabled()) {
                    SendConsoleMessage.debug("Selection: " +selection.toString());
                }

                if(selection.isSuccess()) {
                    Location pos1 = selection.getPos1();
                    Location pos2 = selection.getPos2();

                    if (! sm.isRegionAllWallSigns(pos1, pos2)) {
                        Util.sendMsg(player, ChatColor.RED + "Your sign region selection is not all wall signs or is not 3x3!, please reselect the region!");
                        return true;
                    }
                    fm.setWallSignsLocation(player, pos1, pos2);
                } else {
                    Util.sendMsg(sender, ChatColor.RED + "Region selection is incomplete!");
                }
            }
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("addvotetarget")) {
            VoteManager vm = plugin.getVoteManager();
            vm.handleDailyTargetVote();
            Util.sendMsg(sender, ChatColor.GREEN + "You have added a vote target vote.");
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("rewardplayer")) {
            String playerName = args[1];
            RewardsManager rm = plugin.getRewardsManager();
            Player player = Bukkit.getPlayerExact(playerName);
            if (player != null) {
                rm.rewardPlayer(player);
                Util.sendMsg(sender, ChatColor.AQUA + "Rewarding player " + playerName);
                return true;
            } else {
                Util.sendMsg(sender, ChatColor.RED + "Player is not online!");
                return true;
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("addtestvote")) {
            String playerName = args[1];
            UUID playerUUID = databaseManager.getUUIDFromUsername(playerName);

            databaseManager.addPlayerMonthlyVote(playerUUID, playerName);
            databaseManager.addPlayerAllTimeVote(playerUUID, playerName);
            Util.sendMsg(sender, ChatColor.GREEN + "Test vote added for player " + playerName);
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("convertonlineusernames")) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    databaseManager.convertNamesToUUID(sender, "MONTHLYVOTES", true);
                    databaseManager.convertNamesToUUID(sender, "ALLVOTES", true);
                }
            });
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("convertofflineusernames")) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    databaseManager.convertNamesToUUID(sender, "MONTHLYVOTES", false);
                    databaseManager.convertNamesToUUID(sender, "ALLVOTES", false);
                }
            });
            return true;
        } else if (args.length == 1 && args[0].equalsIgnoreCase("fixusernames")) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    databaseManager.fixPlayerNames(sender);
                }
            });
            return true;
        }  else if (args.length == 1 && args[0].equalsIgnoreCase("checktimedcmds")) {
            RewardsManager rm = plugin.getRewardsManager();
            rm.checkTimedCmds();
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("checkmonthly")) {
            String playerName = args[1];

            int votes = databaseManager.checkUserVotes(playerName, "MONTHLYVOTES");
            if(votes != -1) {
                Util.sendMsg(sender, ChatColor.GREEN + "Player " + playerName + " has a vote count of " + votes + " vote(s) this month.");
            } else {
                Util.sendMsg(sender, ChatColor.RED + "There was an error retrieving votes for player: " + playerName + ", did you type it correctly?");
            }
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("checkalltime")) {
            String playerName = args[1];
            int votes = databaseManager.checkUserVotes(playerName, "ALLVOTES");
            if(votes != -1) {
                Util.sendMsg(sender, ChatColor.GREEN + "Player " + playerName + " has a vote count of " + votes + " vote(s) all time.");
            } else {
                Util.sendMsg(sender, ChatColor.RED + "There was an error retrieving votes for player: " + playerName + ", did you type it correctly?");
            }
            return true;
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("broadcast")) {
            StringBuilder messages = new StringBuilder();

            for (int x = 1; x < args.length; x++) {
                messages.append(args[x]).append(" ");
            }
            String message = messages.toString();
            message = ChatColor.translateAlternateColorCodes('&', message);

            Util.broadcast(message);
            return true;




        } else if (args.length == 3 && args[0].equalsIgnoreCase("sendvotewebhook")) {

            Util.sendMsg(sender, ChatColor.RED + "This is still a work in progress!! Coming soon!");
            return true;
            /*String playername = args[1];
            String website = args[2];
            boolean enabled = plugin.getDiscordFileManager().isVoteWebhookEventEnabled();
            if(enabled) {
                boolean success = plugin.getDiscordWebhookManager().sendVoteNotification(playername, website, "127.0.0.1");

                if(success) {
                    Util.sendMsg(sender, ChatColor.GREEN + "Notification sent successfully!");
                } else {
                    Util.sendMsg(sender, ChatColor.RED + "Notification sending failed, please check the console log!");
                }
            } else {
                Util.sendMsg(sender, ChatColor.RED + "This feature is turned off, please enable and configure it in the discord.yml configuration file");
            }

            return true;*/
        } else {
            Util.sendMsg(sender, plugin.getMessageManager().getUnknownCommandMessage());
            return true;
        }
    }


}
