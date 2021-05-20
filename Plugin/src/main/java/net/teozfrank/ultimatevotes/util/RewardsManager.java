package net.teozfrank.ultimatevotes.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import teozfrank.ultimatevotes.events.VoteRewardEvent;
import net.teozfrank.ultimatevotes.main.UltimateVotes;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Frank
 * Date: 30/08/13
 * Time: 05:41
 * To change this template use File | Settings | File Templates.
 */
public class RewardsManager {

    private UltimateVotes plugin;
    private List<TimedCmd> timedCommands;

    public RewardsManager(UltimateVotes plugin) {
        this.plugin = plugin;
        this.timedCommands = new ArrayList<TimedCmd>();
    }

    /**
     * method to check if a player is online
     */
    public boolean isPlayerOnline(String p) {
        Player pl = Bukkit.getPlayer(p);

        if (pl != null) {
            return true;
        } else {
            return false;
        }

    }

    public boolean hasUnclaimedRewards(UUID playerUUID) {
        DatabaseManager mysql = plugin.getDatabaseManager();
        int unclaimedRewards = mysql.checkUserUnclaimedVotes(playerUUID);
        if (plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Unclaimed rewards value for player " + playerUUID + " is " + unclaimedRewards);
        }
        if (unclaimedRewards > 0) {
            return true;
        }
        return false;
    }

    public void rewardRepeating(String playerName) {
        FileManager fm = plugin.getFileManager();
        List<String> repeatingRewards = fm.getRewards().getStringList("repeatingrewards.default");
        if (plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("repeating rewards size: " + repeatingRewards.size());
            SendConsoleMessage.debug("repeating rewards: " + repeatingRewards.toString());
        }
        for (String s : repeatingRewards) {
            s = s.replaceAll("%player%", playerName);
            if (s.contains(";")) {
                if (plugin.isDebugEnabled()) {
                    SendConsoleMessage.debug("Special command!");
                }
                handleSpecialCmd(s);
            } else {
                if (plugin.isDebugEnabled()) {
                    SendConsoleMessage.debug("dispatching vote reward: " + s);
                }
                Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), s);
            }
        }
    }

    /**
     * reward repeating rewards by world name
     *
     * @param playerName the players name
     * @param world      the world name of the player
     */
    private void rewardRepeatingByWorld(String playerName, String world) {
        FileManager fm = plugin.getFileManager();
        List<String> repeatingRewards = fm.getRewards().getStringList("repeatingrewards." + world);
        for (String s : repeatingRewards) {
            s = s.replaceAll("%player%", playerName);
            if (s.contains(";")) {
                if (plugin.isDebugEnabled()) {
                    SendConsoleMessage.debug("Special command!");
                }
                handleSpecialCmd(s);
            } else {
                if (plugin.isDebugEnabled()) {
                    SendConsoleMessage.debug("dispatching vote reward: " + s);
                }
                Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), s);
            }
        }
    }

    public int getStartNum(int votes, int unclaimedVotes) {
        return (votes - unclaimedVotes) + 1;
    }

    /**
     * Get a list of rewards to check
     * @param votes the current vote count the player has
     * @param unclaimedvotes the amount of unclaimed votes the player has
     * @param claimAmount the amount of rewards wanting to be claimed. -1 means all.
     * @return a list of cumulative rewards to check for
     */
    public List<Integer> getCumulativeRewardListOfRewardsToCheckFor(int votes, int unclaimedvotes, int claimAmount) {

        int stopNum = -1;
        int startNum = getStartNum(votes, unclaimedvotes);
        List<Integer> listOfCumulativeRewardsToCheckFor = new ArrayList<>();

        if(claimAmount > 0) {
            stopNum = startNum + claimAmount;
        } else {
            stopNum = votes + 1;
        }

        for(int x = startNum; x < stopNum; x++) {
            listOfCumulativeRewardsToCheckFor.add(x);
        }

        return listOfCumulativeRewardsToCheckFor;
    }

    /*public void rewardAmount(String player, int votes, int unclaimedVotes) {
        FileManager fm = plugin.getFileManager();
        Player p = Bukkit.getPlayer(player);

        if (p == null) {
            if(plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Player not online not rewarding");
            }
            return;// not online
        }

        int startNum = votes - unclaimedVotes;

        if (plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Start num: " + startNum);
        }

        for (int x = startNum; x < votes; x++) {

            int offset = x + 1;
            if (plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Checking reward for " + offset + " votes.");
            }
            if (fm.hasVoteReward(offset)) {
                List<String> rewards = fm.getRewards().getStringList("rewards." + offset);
                for (String s : rewards) {

                    String snew = s.replaceAll("%player%", p.getName());
                    if (snew.contains(";")) {
                        if (plugin.isDebugEnabled()) {
                            SendConsoleMessage.debug("Special command!");
                        }
                        handleSpecialCmd(snew);
                    } else {
                        if (plugin.isDebugEnabled()) {
                            SendConsoleMessage.debug("dispatching vote reward: " + snew);
                        }
                        Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), snew);
                    }

                }
            }
        }
    }*/


    public void rewardAmount(String player, int votes, int unclaimedVotes, int claimAmount) {
        FileManager fm = plugin.getFileManager();
        Player p = Bukkit.getPlayer(player);

        if (p == null) {
            if(plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Player not online not rewarding");
            }
            return;// not online
        }

        List<Integer> cumulativeRewardsToCheckFor = getCumulativeRewardListOfRewardsToCheckFor(votes, unclaimedVotes,
                claimAmount);

        for (int cumulativeReward: cumulativeRewardsToCheckFor) {
            if (plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Checking cumulative reward for " + cumulativeReward + " votes.");
            }
            if (fm.hasVoteReward(cumulativeReward)) {
                List<String> rewards = fm.getRewards().getStringList("rewards." + cumulativeReward);
                for (String s : rewards) {

                    String snew = s.replaceAll("%player%", p.getName());
                    if (snew.contains(";")) {
                        if (plugin.isDebugEnabled()) {
                            SendConsoleMessage.debug("Special command!");
                        }
                        handleSpecialCmd(snew);
                    } else {
                        if (plugin.isDebugEnabled()) {
                            SendConsoleMessage.debug("dispatching vote reward: " + snew);
                        }
                        Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), snew);
                    }

                }
            }
        }
    }

    public void addTimedCmd(TimedCmd timedCmd) {
        this.timedCommands.add(timedCmd);
    }

    private void handleSpecialCmd(String s) {
        String durationPercentString;

        String[] commandRaw = s.split(";");

        durationPercentString = commandRaw[0];

        for (String cmd : commandRaw) {
            if(plugin.isDebugEnabled()) {
                SendConsoleMessage.debug(cmd);
            }
        }

        int arraySize = commandRaw.length - 1; //we want to create a new array just with the commands with a size one less than the raw reward string
        String[] commandlist = new String[arraySize];//create the array with size one less

        int commandListIndex = 0;//the starting index of the new array

        for (int x = 1; x < commandRaw.length; x++) {
            commandlist[commandListIndex] = commandRaw[x];
            commandListIndex++;
        }

        if (durationPercentString.contains("/")) {
            handlePercentCmd(durationPercentString, commandlist);
        } else {
            handleTimedCmd(durationPercentString, commandlist);
        }

    }

    private void handlePercentCmd(String durationPercentString, String[] cmds) {
        try {
            String[] strings = durationPercentString.split("/");
            int leftPercentChance = Integer.parseInt(strings[0]);
            int rightPercentChance = Integer.parseInt(strings[1]);

            Random random = new Random();
            int randomChanceRight = random.nextInt(rightPercentChance);
            if(plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Random chance right: " + randomChanceRight);
            }
            for(int chance = 1; chance <= leftPercentChance; chance ++) {
                int randomChanceLeft = random.nextInt(rightPercentChance);
                if(plugin.isDebugEnabled()) {
                    SendConsoleMessage.debug("Random chance left: " + randomChanceLeft + "/" + randomChanceRight );
                }
                if(randomChanceLeft == randomChanceRight) {
                    if(plugin.isDebugEnabled()) {
                        SendConsoleMessage.debug("Chance met, rewarding player!");
                    }

                    for (String command : cmds) {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
                    }
                    break;
                }
            }

        } catch(Exception e) {

        }
    }

    private void handlePercentCmdold(String durationPercentString, String[] cmds) {
        if(plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Cmd length:" + cmds.length);
        }

        try {
            String[] strings = durationPercentString.split("/");
            int leftPercentChance = Integer.parseInt(strings[0]);
            int rightPercentChance = Integer.parseInt(strings[1]);

            Random random = new Random();
            int randomNumber = random.nextInt(rightPercentChance);
            if (plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Chance is: " + leftPercentChance + "/" + rightPercentChance);
                SendConsoleMessage.debug("Random Number is: " + randomNumber);
            }
            if (leftPercentChance == 1 && rightPercentChance == 1) {
                if (plugin.isDebugEnabled()) {
                    SendConsoleMessage.debug("Chance command ran!");
                }
                for (String command : cmds) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
                }
            } else {
                for (int x = 0; x <= leftPercentChance; x++) {
                    if (x == randomNumber) {
                        if (plugin.isDebugEnabled()) {
                            SendConsoleMessage.debug("Chance command ran!");
                        }
                        for (String command : cmds) {
                            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
                        }
                    }
                }
            }
        } catch (Exception e) {
            SendConsoleMessage.error("Error parsing percent cmd list! " + e.getMessage());
        }
    }

    private void handleTimedCmd(String durationPercentString, String[] cmds) {
        Long currentTime = System.currentTimeMillis();
        List<TimedCmd> timedCmdsToRemove = new ArrayList<TimedCmd>();
        try {
            int duration = Integer.parseInt(durationPercentString);
            if (plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Current time:" + currentTime);
                SendConsoleMessage.debug("Duration:" + duration);
                SendConsoleMessage.debug("command list next:");
                Util.printArray(cmds);
            }
            TimedCmd timedCmd = new TimedCmd(currentTime, duration, cmds);

            for (TimedCmd timedCmds : getTimedCommands()) {//loop through the timed command list to check for duplicate commands
                if (timedCmds.getCmds() != null) {
                    if (timedCmd.getCmds().equals(cmds)) {
                        if (plugin.isDebugEnabled()) {
                            SendConsoleMessage.debug("Command already exists timed cmds removing!");
                            Util.printArray(timedCmd.getCmds());
                        }
                        timedCmdsToRemove.add(timedCmds);
                    }
                }
            }

            for (TimedCmd timedCmdToRemove : timedCmdsToRemove) {
                getTimedCommands().remove(timedCmdToRemove);
            }

            addTimedCmd(timedCmd);
            if (plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Timed command added!");
            }

        } catch (NumberFormatException e) {
            SendConsoleMessage.warning("Timed command duration parsing failed!");
        }
    }

    /**
     * reward by vote amount by world
     *
     * @param playerName the players name
     * @param world      the world name of the player
     * @param voteCount      the amount of votes the player has
     */
    /*private void rewardAmountByWorld(String playerName, String world, int votes, int unclaimedVotes) {
        FileManager fm = plugin.getFileManager();
        Player p = Bukkit.getPlayer(playerName);

        if (p == null) {
            if(plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Player not online not rewarding");
            }
            return;// not online
        }

        int startNum = votes - unclaimedVotes;
        if (plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Start num: " + startNum);
        }

        for (int x = startNum; x < votes; x++) {
            int offset = x + 1;
            if (plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Checking reward for " + offset + " votes.");
            }
            if (fm.hasVoteRewardByWorld(world, offset)) {
                List<String> rewards = fm.getRewards().getStringList("rewards." + world + "." + offset);
                for (String s : rewards) {
                    s = s.replaceAll("%player%", p.getName());
                    if (s.contains(";")) {
                        if (plugin.isDebugEnabled()) {
                            SendConsoleMessage.debug("Special command!");
                        }
                        handleSpecialCmd(s);
                    } else {
                        if (plugin.isDebugEnabled()) {
                            SendConsoleMessage.debug("dispatching vote reward: " + s);
                        }
                        Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), s);
                    }
                }
            }
        }
    }*/

    private void rewardAmountByWorld(String playerName, String world, int voteCount, int unclaimedVotes, int claimAmount) {
        FileManager fm = plugin.getFileManager();
        Player p = Bukkit.getPlayer(playerName);

        if (p == null) {
            if(plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Player not online not rewarding");
            }
            return;// not online
        }

        List<Integer> cumulativeRewardsToCheckFor = getCumulativeRewardListOfRewardsToCheckFor(voteCount, unclaimedVotes,
                claimAmount);

        for (int cumulativeReward: cumulativeRewardsToCheckFor) {
            if (plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Checking cumulative reward for " + cumulativeReward + " votes.");
            }
            if (fm.hasVoteRewardByWorld(world, cumulativeReward)) {
                List<String> rewards = fm.getRewards().getStringList("rewards." + world + "." + cumulativeReward);
                for (String s : rewards) {
                    s = s.replaceAll("%player%", p.getName());
                    if (s.contains(";")) {
                        if (plugin.isDebugEnabled()) {
                            SendConsoleMessage.debug("Special command!");
                        }
                        handleSpecialCmd(s);
                    } else {
                        if (plugin.isDebugEnabled()) {
                            SendConsoleMessage.debug("dispatching vote reward: " + s);
                        }
                        Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), s);
                    }
                }
            }
        }
    }

    public boolean rewardPlayerByClaimAmount(Player player, int unclaimedVotesCount, int claimAmount) {
        DatabaseManager databaseManager = plugin.getDatabaseManager();
        MessageManager mm = plugin.getMessageManager();
        String playerName = player.getName();
        UUID playerUUID = player.getUniqueId();
        String world = player.getWorld().getName();
        FileManager fm = plugin.getFileManager();

        String table = "MONTHLYVOTES";
        if(! plugin.getFileManager().rewardByMonthlyVotesCount()) {
            table = "ALLVOTES";
        }

        int voteCount = databaseManager.checkUserVotes(playerUUID, table);
        if (plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Player " + playerName + " has an offline reward value of " + unclaimedVotesCount + ".");
        }

        VoteRewardEvent event = new VoteRewardEvent(player, voteCount, unclaimedVotesCount);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if(plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("CALL VOTE REWARD EVENT");
        }

        if(event.isCancelled()) {
            return true;
        }

        Util.sendMsg(player, mm.getRewardMessage());

        if (fm.rewardByWorld()) {//if we are rewarding by world
            for (int x = 0; x < claimAmount; x++) {
                rewardRepeatingByWorld(playerName, world);
            }
            rewardAmountByWorld(playerName, world, voteCount, unclaimedVotesCount, claimAmount);
        } else {
            for (int x = 0; x < claimAmount; x++) {
                rewardRepeating(playerName);
            }
            rewardAmount(playerName, voteCount, unclaimedVotesCount, claimAmount);
        }

        String message = mm.getClaimMessage();
        message = message.replaceAll("%player%", playerName);
        Util.broadcast(message);

        return true;
    }

    public void rewardPlayer(final Player player) {
        final DatabaseManager databaseManager = plugin.getDatabaseManager();
        final MessageManager mm = plugin.getMessageManager();
        final String playerName = player.getName();
        final UUID playerUUID = player.getUniqueId();
        final String world = player.getWorld().getName();
        final FileManager fm = plugin.getFileManager();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

            @Override
            public void run() {
                final int unclaimedVoteCount = databaseManager.checkUserUnclaimedVotes(playerUUID);
                String table = "MONTHLYVOTES";
                if(! plugin.getFileManager().rewardByMonthlyVotesCount()) {
                    table = "ALLVOTES";
                }
                final int voteCount = databaseManager.checkUserVotes(playerUUID, table);

                if (hasUnclaimedRewards(playerUUID)) {
                    if (plugin.isDebugEnabled()) {
                        SendConsoleMessage.debug("Player " + playerName + " has an unclaimed reward value of " + unclaimedVoteCount + ".");
                    }
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

                        @Override
                        public void run() {

                            VoteRewardEvent event = new VoteRewardEvent(player, voteCount, unclaimedVoteCount);
                            Bukkit.getServer().getPluginManager().callEvent(event);
                            if(plugin.isDebugEnabled()) {
                                SendConsoleMessage.debug("CALL VOTE REWARD EVENT");
                            }

                            if(event.isCancelled()) {
                                return;
                            }
                            Util.sendMsg(player, mm.getRewardMessage());

                            try {
                                if (fm.rewardByWorld()) {//if we are rewarding by world
                                    if (plugin.isDebugEnabled()) {
                                        SendConsoleMessage.debug("reward by world.");
                                    }
                                    for (int x = 0; x < unclaimedVoteCount; x++) {
                                        rewardRepeatingByWorld(playerName, world);
                                    }
                                    rewardAmountByWorld(playerName, world, voteCount, unclaimedVoteCount, -1);
                                } else {
                                    for (int x = 0; x < unclaimedVoteCount; x++) {
                                        if (plugin.isDebugEnabled()) {
                                            SendConsoleMessage.debug("reward repeating.");
                                        }
                                        rewardRepeating(playerName);
                                    }
                                    rewardAmount(playerName, voteCount, unclaimedVoteCount, -1);
                                }
                            } catch (Exception e) {
                                SendConsoleMessage.error("Error trying to reward player: " + e.getMessage());
                            }

                            databaseManager.resetUnclaimedReward(playerUUID);
                            if (fm.useClaimCommand() && !fm.rewardOnline()) {
                                String message = mm.getClaimMessage();
                                message = message.replaceAll("%player%", playerName);
                                Util.broadcast(message);
                            }

                        }
                    }, 1L);

                }
            }
        });
    }

    /**
     * get a list of timed commands
     *
     * @return a list of timed commands
     */
    public List<TimedCmd> getTimedCommands() {
        return timedCommands;
    }

    /**
     * check each timed command and check does it
     * need to be ran
     */
    public void checkTimedCmds() {
        List<TimedCmd> timedCmds = this.getTimedCommands();
        List<TimedCmd> timedCmdsToRemove = new ArrayList<TimedCmd>();

        if (timedCmds.isEmpty()) {
            if (plugin.isDebugEnabled()) {
                //SendConsoleMessage.debug("Timed Cmds Empty!");
            }
            return;
        }

        if (plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("timedcmd size: " + timedCmds.size());
            for (TimedCmd timedCmd : timedCmds) {
                Util.printTimedCmd(timedCmd);
            }
        }

        for (TimedCmd timedCmd : timedCmds) {
            long difference = System.currentTimeMillis() - timedCmd.getStartTime();

            int mins = (int) TimeUnit.MILLISECONDS.toMinutes(difference);

            if (plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Mins: " + mins);
            }

            if (mins >= timedCmd.getDuration()) {
                for (String command : timedCmd.getCmds()) {
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
                }
                timedCmdsToRemove.add(timedCmd);
            }
            if (plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Difference: " + difference);
                SendConsoleMessage.debug("mins: " + mins);
            }
        }

        for (TimedCmd timedCmd : timedCmdsToRemove) {
            timedCmds.remove(timedCmd);
        }

        timedCmdsToRemove.clear();
    }


}