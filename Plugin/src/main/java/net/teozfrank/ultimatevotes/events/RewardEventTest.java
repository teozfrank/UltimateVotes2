package net.teozfrank.ultimatevotes.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.SendConsoleMessage;
import teozfrank.ultimatevotes.events.VoteRewardEvent;

/**
 * Created by Frank on 06-Aug-17.
 */
public class RewardEventTest implements Listener {

    private UltimateVotes plugin;

    public RewardEventTest(UltimateVotes plugin) {
        this.plugin = plugin;
        //plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerRewarded(VoteRewardEvent e) {
        SendConsoleMessage.debug("PLAYER REWARDED: " + e.getPlayer().getName());
    }

}
