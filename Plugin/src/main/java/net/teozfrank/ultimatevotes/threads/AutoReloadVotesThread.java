package net.teozfrank.ultimatevotes.threads;

import org.bukkit.entity.Player;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.DatabaseManager;
import net.teozfrank.ultimatevotes.util.SendConsoleMessage;
import net.teozfrank.ultimatevotes.util.VoteManager;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Frank
 * Date: 08/07/13
 * Time: 15:24
 * To change this template use File | Settings | File Templates.
 */
public class AutoReloadVotesThread implements Runnable {


    ArrayList<Player> announcedPlayers = new ArrayList<Player>();

    UltimateVotes plugin;

    public AutoReloadVotesThread(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {

        final VoteManager vm = plugin.getVoteManager();
        final DatabaseManager databaseManager = plugin.getDatabaseManager();

        final Date date = new Date();
        if(plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Reloading Cache.");
        }
        vm.allVotes = databaseManager.voteAllTime();
        vm.monthlyVotes = databaseManager.voteMonthly();
        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {

            @Override
            public void run() {
                plugin.getSignManager().updateTopVotersOnWall();
            }
        });
        plugin.setLastVotesUpdate(date.getTime());
        if(plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Reloading Complete.");
        }
    }
}
