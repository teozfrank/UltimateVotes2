package net.teozfrank.ultimatevotes.threads;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.FileManager;
import net.teozfrank.ultimatevotes.util.SendConsoleMessage;
import net.teozfrank.ultimatevotes.util.SignManager;
import net.teozfrank.ultimatevotes.util.WallOfSignsPositions;

/**
 * Created by frank on 02/07/2014.
 */
public class WallSignIntroThread implements Runnable {

    final UltimateVotes plugin;

    public WallSignIntroThread(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        final SignManager sm = plugin.getSignManager();


        if(plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("running first task!");
        }
        sm.showCreditsSign(WallOfSignsPositions.CENTER); //show the credits sign in the center of the wall.

        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                Location wallSignLocation = sm.getWallSignLocation(WallOfSignsPositions.CENTER);
                if(plugin.isDebugEnabled()) {
                    SendConsoleMessage.debug("running second task!");
                    SendConsoleMessage.debug(wallSignLocation+"");
                }
                sm.clearSignWall();
                sm.updateTopVotersOnWall();
            }
        }, 100);


    }
}
