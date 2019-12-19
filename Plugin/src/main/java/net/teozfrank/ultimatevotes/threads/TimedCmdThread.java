package net.teozfrank.ultimatevotes.threads;

import net.teozfrank.ultimatevotes.main.UltimateVotes;
/**
 * Created by Frank on 15/02/2015.
 */
public class TimedCmdThread implements Runnable {

    private UltimateVotes plugin;

    public TimedCmdThread(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getRewardsManager().checkTimedCmds();
    }
}
