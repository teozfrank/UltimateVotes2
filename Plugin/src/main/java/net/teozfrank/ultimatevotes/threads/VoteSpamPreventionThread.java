package net.teozfrank.ultimatevotes.threads;

import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.SendConsoleMessage;

public class VoteSpamPreventionThread implements Runnable {

    private UltimateVotes plugin;

    public VoteSpamPreventionThread(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        int votePreventionSize = plugin.getUtil().getVotedPlayers().size();

        if(votePreventionSize > 0) {
            plugin.getUtil().clearVotedPlayers();
            if(plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Clearing vote spam prevention.");
            }
        }
    }
}
