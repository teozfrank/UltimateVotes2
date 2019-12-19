package net.teozfrank.ultimatevotes.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import net.teozfrank.ultimatevotes.main.UltimateVotes;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Frank on 05/01/2015.
 */
public class ChannelListener implements PluginMessageListener {

    private UltimateVotes plugin;

    public ChannelListener(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        RewardsManager rm = plugin.getRewardsManager();
        FileManager fm = plugin.getFileManager();
        VoteManager vm = plugin.getVoteManager();
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            final String subchannel = in.readUTF();

            if(plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("Subchannel name: " + subchannel);
            }

            if(subchannel.equals(UltimateVotes.INCOMING_CHANNEL_NAME)) {
                String playerUUID = in.readUTF();
                if(plugin.isDebugEnabled()) {
                    SendConsoleMessage.debug("player UUID: " + playerUUID);
                }
                Player votePlayer = Bukkit.getPlayer(UUID.fromString(playerUUID));

                if(player == null) {
                    if (plugin.isDebugEnabled()) {
                        SendConsoleMessage.debug("player is null! ");
                    }
                    return;
                }
                UUID playerUUIDOut = player.getUniqueId();

                if(fm.useClaimCommand()) {
                    if(plugin.isDebugEnabled()) {
                        SendConsoleMessage.debug("Player is online, vote claiming is enabled, not rewarding.");
                    }
                    return;
                }

                if(!fm.isRewardsEnabled()) {
                    if(plugin.isDebugEnabled()) {
                        SendConsoleMessage.debug("Player is online, vote rewards are disabled, not rewarding.");
                    }
                    return;
                }

                if(rm.hasUnclaimedRewards(playerUUIDOut)) { //if the player has offline rewards and we are not using the claim command
                    if(plugin.isDebugEnabled()) {
                        SendConsoleMessage.debug("rewarding player.");
                    }
                    vm.handleNotVoted(playerUUIDOut);
                    rm.rewardPlayer(votePlayer);
                }


            }
        } catch (IOException e) {
            //ignored
        }
    }
}
