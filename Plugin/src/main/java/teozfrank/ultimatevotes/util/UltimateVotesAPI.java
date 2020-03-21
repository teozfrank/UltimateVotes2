package teozfrank.ultimatevotes.util;

import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.SendConsoleMessage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Copyright teozfrank / FJFreelance 2014 All rights reserved.
 */
public class UltimateVotesAPI {


    public UltimateVotesAPI() {

    }

    /**
     * get the players monthly votes count
     * accesses the database, should be called async, not on the main server thread!
     * @param playerUUID the players UUID
     * @return the amount of votes a player has for this month
     */
    public static int getPlayerMonthlyVotes(UUID playerUUID) {
       return UltimateVotes.getDatabaseManager().checkUserVotes(playerUUID, "MONTHLYVOTES");
    }

    /**
     * get the players vote count by username
     * accesses the database, should be called async, not on the main server thread!
     * @param playername the players username
     * @return the amount of votes a player has for this month
     */
    public static int getPlayerMonthlyVotes(String playername) {
        return UltimateVotes.getDatabaseManager().checkUserVotes(playername, "MONTHLYVOTES");
    }

    /**
     * get the players total votes in total
     * accesses the database, should be called async, not on the main server thread!
     * @param playerUUID the players UUID
     * @return the amount of votes a player has in total
     */
    public static int getPlayerAllTimeVotes(UUID playerUUID) {
       return UltimateVotes.getDatabaseManager().checkUserVotes(playerUUID, "ALLVOTES");
    }

    /**
     * get the players total votes in total
     * accesses the database, should be called async, not on the main server thread!
     * @param playername the players username
     * @return the amount of votes a player has in total
     */
    public static int getPlayerAllTimeVotes(String playername) {
        return UltimateVotes.getDatabaseManager().checkUserVotes(playername, "ALLVOTES");
    }

    /**
     * has the player voted today
     * accesses the database, should be called async, not on the main server thread!
     * @param playerUUID the player UUID
     * @return true if they have voted, false if not
     */
    public static boolean hasVotedToday(UUID playerUUID) {
        return UltimateVotes.getDatabaseManager().hasVotedToday(playerUUID);
    }

    /**
     * hooks into the plugins way of keeping track of if a player has voted today
     * requires no need to access the database safe to use in sync with the main
     * server thread
     * @param playerUUID the player uuid to check if they have voted today
     * @return true if they have voted today, false if not
     */
    public static boolean hasVotedTodayCached(UUID playerUUID) {
        return UltimateVotes.getVoteManager().hasVotedToday.containsKey(playerUUID);
    }

    /**
     * hooks into the plugins way of keeping track of the list of online players
     * that have voted today requires no need to access the database safe to use
     * in sync with the main server thread
     * @return a list of online player uuids that have voted today
     */
    public ArrayList<UUID> getListOfPlayersWhoHaveVotedToday() {
        ArrayList<UUID> hasVotedTodayList = new ArrayList<UUID>();
        for(UUID key: UltimateVotes.getVoteManager().hasVotedToday.keySet()) {
            hasVotedTodayList.add(key);
        }
        return hasVotedTodayList;
    }

    /**
     * hooks into the plugins way of keeping track of the list of online players
     * that have note voted today requires no need to access the database safe to use
     * in sync with the main server thread
     * @return a list of online player uuids that have not voted today
     */
    public ArrayList<UUID> getListOfPlayersWhoHaveNotVotedToday() {
        return UltimateVotes.getVoteManager().hasNotVotedToday;
    }


    /**
     * has the player voted today
     * accesses the database, should be called async, not on the main server thread!
     * @param playername the players username
     * @return true if they have voted, false if not
     */
    public static boolean hasVotedToday(String playername) {
        UUID playerUUID = UltimateVotes.getDatabaseManager().getUUIDFromUsername(playername);
        if(playerUUID != null) {
            return UltimateVotes.getDatabaseManager().hasVotedToday(playerUUID);
        }
        return false;
    }

    /**
     * get the top 10 voters from the database
     * accesses the database, should be called async, not on the main server thread!
     * @return the top 10 voters from the database
     */
    public static LinkedHashMap<String, Integer> getTopVotersMonthly() {
        return UltimateVotes.getDatabaseManager().voteMonthly();
    }

    /**
     * get the top 10 voters all time from the database
     * accesses the database, should be called async, not on the main server thread!
     * @return the top 10 voters all time from the database
     */
    public static LinkedHashMap<String, Integer> getTopVotersAllTime() {
        return UltimateVotes.getDatabaseManager().voteAllTime();
    }

    /**
     * get the top monthly voters based on a limit passed in
     * accesses the database, should be called async, not on the main server thread!
     * @param limit the limit of the top voters
     * @return a linked hashmap of the top players
     */
    public static LinkedHashMap<String, Integer> getTopVotersMonthly(int limit) {
        if(limit <= 0) {
            SendConsoleMessage.error("API ACCESS: Top monthly player limit too low! limited value was: " + limit);
            return null;
        }
        return UltimateVotes.getDatabaseManager().voteMonthly(limit);
    }

    /**
     * get the top all time voters based on a limit passed in
     * accesses the database, should be called async, not on the main server thread!
     * @param limit the limit of the top voters
     * @return a linked hashmap of the top players
     */
    public static LinkedHashMap<String, Integer> getTopVotersAllTime(int limit) {
        if(limit <= 0) {
            SendConsoleMessage.error("API ACCESS: Top all time player limit too low! limited value was: " + limit);
            return null;
        }
        return UltimateVotes.getDatabaseManager().voteAllTime(limit);
    }

    /**
     * perform a mysql query on the database of ultimatevotes
     * please remember the structure of the database can change
     * breaking code relying on this, please only use this as
     * as last resort for something the API does not provide
     * accesses the database, should be called async, not on the main server thread!
     * @param query the mysql query
     * @return the resulting resultset of the database query
     */
    public static ResultSet performDatabaseQuery(String query) {
        try {
            Connection connection = UltimateVotes.getDatabaseManager().getConnection();
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            SendConsoleMessage.error("API ACCESS QUERY ERROR: " + e.getMessage());
        }
        return null;
    }

    /**
     * get the top 10 monthly voters from the cached value from the database
     * @return the top 10 monthly voters from the cached value
     */
    public static LinkedHashMap<String, Integer> getTopVotersMonthlyCached() {
        return UltimateVotes.getVoteManager().getMonthlyVotes();
    }

    /**
     * get the top 10 all time voters from the cached value from the database
     * @return the top 10 monthly voters from the cached value
     */
    public static LinkedHashMap<String, Integer> getTopVotersAllTimeCached() {
        return UltimateVotes.getVoteManager().getAllTimeVotes();
    }

    /**
     * get the last time the cached values were updated from the database
     * @return the time in long format
     */
    public static Long getLastUpdatedTime() {
        return UltimateVotes.getLastVotesUpdate();
    }

    /**
     * get the players total votes in total
     * @param playerUUID the players UUID
     * @return the amount of votes a player has in total
     */
    @Deprecated
    public static int getPlayerTotalVotes(UUID playerUUID) {
        return UltimateVotes.getDatabaseManager().checkUserVotes(playerUUID, "ALLVOTES");
    }

    /**
     * get the players total votes in total
     * @param playername the players username
     * @return the amount of votes a player has in total
     */
    @Deprecated
    public static int getPlayerTotalVotes(String playername) {
        return UltimateVotes.getDatabaseManager().checkUserVotes(playername, "ALLVOTES");
    }

    /**
     * get the top 10 voters from the database
     * @return the top 10 voters from the database
     */
    @Deprecated
    public static LinkedHashMap<String, Integer> getTopPlayers() {
        return UltimateVotes.getDatabaseManager().voteMonthly();
    }

    /**
     * get the top 10 voters from the cached value from the database
     * @return the top 10 voters from the cached value
     */
    @Deprecated
    public static LinkedHashMap<String, Integer> getTopPlayersCached() {
        return UltimateVotes.getVoteManager().getMonthlyVotes();
    }

}
