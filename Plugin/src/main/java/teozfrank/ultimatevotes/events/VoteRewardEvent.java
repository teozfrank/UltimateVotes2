package teozfrank.ultimatevotes.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Frank on 02-Jan-17.
 * All Rights Reserved
 */
public class VoteRewardEvent extends Event implements Cancellable {

    private Player player;
    private int voteCount;
    private int unclaimedCount;
    private boolean isCancelled;
    private static final HandlerList handlerList = new HandlerList();

    public VoteRewardEvent(Player player, int voteCount, int unclaimedCount) {
        this.player = player;
        this.voteCount = voteCount;
        this.unclaimedCount = unclaimedCount;
    }

    /**
     * get the vote count of the player before they are rewarded
     * @return the vote count of the player
     */
    public int getVoteCount() {
        return voteCount;
    }


    /**
     * get the unclaimed vote count of the player before they are rewarded
     * @return the unclaimed vote count
     */
    public int getUnclaimedCount() {
        return unclaimedCount;
    }

    /**
     * get the player who is going to be rewarded
     * @return the player who is going to be rewarded
     */
    public Player getPlayer() {
        return player;
    }

    /**
     *
     * @return
     */
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * is the event cancelled
     * @return true if cancelled false if not
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * set the cancel state of the event
     * @param b true if cancelled false if not
     */
    @Override
    public void setCancelled(boolean b) {
        this.isCancelled = b;
    }
}
