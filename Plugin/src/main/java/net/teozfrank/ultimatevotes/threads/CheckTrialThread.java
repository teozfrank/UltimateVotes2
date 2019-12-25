package net.teozfrank.ultimatevotes.threads;

import org.bukkit.scheduler.BukkitRunnable;
import net.teozfrank.ultimatevotes.main.UltimateVotes;

/**
 * Created by Frank on 05/06/2015.
 */
public class CheckTrialThread implements Runnable {

    private UltimateVotes plugin;

    public CheckTrialThread(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        //plugin.checkTrial();
    }
}
