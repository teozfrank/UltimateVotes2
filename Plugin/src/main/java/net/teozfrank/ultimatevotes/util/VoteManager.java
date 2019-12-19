package net.teozfrank.ultimatevotes.util;


import java.sql.Date;
import java.util.*;

import org.bukkit.Bukkit;
import net.teozfrank.ultimatevotes.main.UltimateVotes;

public class VoteManager {

    public LinkedHashMap<String, Integer> allVotes;
    public LinkedHashMap<String, Integer> monthlyVotes;
    public LinkedHashMap<String, Integer> TopFiveDailyVotes;
    public HashMap<UUID, Date> hasVotedToday;
    public ArrayList<UUID> hasNotVotedToday;


    UltimateVotes plugin;

    public VoteManager(UltimateVotes plugin) {
        this.plugin = plugin;
        this.allVotes = new LinkedHashMap<String, Integer>();
        this.monthlyVotes = new LinkedHashMap<String, Integer>();
        //this.TopFiveDailyVotes = new LinkedHashMap<String, Integer>();
        this.hasVotedToday = new HashMap<UUID, Date>();
        this.hasNotVotedToday = new ArrayList<UUID>();
    }

    public LinkedHashMap<String, Integer> getMonthlyVotes() {
        return monthlyVotes;
    }

    public LinkedHashMap<String, Integer> getAllTimeVotes() { return allVotes;}

    public void handleDailyTargetVote() {
        FileManager fm = plugin.getFileManager();
        DatabaseManager databaseManager = plugin.getDatabaseManager();

        int dailyVoteTarget = fm.getDailyVoteTarget();
        int dailyVoteCount = databaseManager.getDailyVoteTargetCount();

        if (plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("daily vote target: " + dailyVoteTarget);
            SendConsoleMessage.debug("daily vote count: " + dailyVoteCount);
            SendConsoleMessage.debug("daily vote count: " + dailyVoteCount);
        }
        Date date = new Date(System.currentTimeMillis());
        Date lastReached = databaseManager.getDailyVoteTargetLastReached();

        if (plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("compare: " + date.compareTo(lastReached));
        }


        if (date.compareTo(lastReached)==0) {
            if (plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Daily vote target reached! not adding more!");
            }
            return;
        } else {
            if (plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Daily vote target not reached adding vote.");
            }
        }

        if (dailyVoteCount < dailyVoteTarget) {
            if (databaseManager.getDailyVoteTargetLastReset().equals(date)) {//if the last reset date is not today.
                if (plugin.isDebugEnabled()) {
                    SendConsoleMessage.debug("Resetting daily vote target.");
                }
                databaseManager.resetDailyVoteTarget(false);//reset the daily vote target with false to indicate that the target was not met
            }
            databaseManager.addVoteToDailyTarget();//add the vote to the daily target.
            int dailyVoteCountNew = databaseManager.getDailyVoteTargetCount();

            if (plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("new daily target vote count: " + dailyVoteCountNew);
            }

            if (fm.hasVoteTargetAnnounceCommands(dailyVoteCountNew)) {
                List<String> commands = fm.getVoteTargetAnnounceCommands(dailyVoteCountNew);

                for (String command : commands) {
                    if (plugin.isDebugEnabled()) {
                        SendConsoleMessage.debug("dispatching vote target announce command: " + command);
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
            } else {
                if (plugin.isDebugEnabled()) {
                    SendConsoleMessage.debug("Does not have a vote target announce command.");
                }
            }

            if (dailyVoteCountNew == dailyVoteTarget) {
                if (plugin.isDebugEnabled()) {
                    SendConsoleMessage.debug("Vote target REACHED!");
                }
                databaseManager.resetDailyVoteTarget(true);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fm.getVoteTargetAnnouncement());
                /*for (String players : hasVotedToday) {
                    Player player = Bukkit.getPlayer(players);
                    if (player != null) {
                        List<String> rewards = fm.getDailyTargetRewards();
                        for (String reward : rewards) {
                            reward = reward.replaceAll("%player%", player.getName());
                            if (plugin.isDebugEnabled()) {
                                SendConsoleMessage.debug("Dispatching daily vote target rewards: " + reward);
                            }
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward);
                        }
                    }
                }*/
            }
        }


    }

    /**
     * handle players who have not voted and add them to the has voted list
     * @param playerUUID the players uuid
     */
    public void handleNotVoted(UUID playerUUID) {
        if (hasNotVotedToday.contains(playerUUID)) {
            hasNotVotedToday.remove(playerUUID);
            if(plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("adding player " + playerUUID + " to has voted list.");
            }
            hasVotedToday.put(playerUUID, new Date(System.currentTimeMillis()));
        }
    }


}
