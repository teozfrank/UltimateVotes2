package net.teozfrank.ultimatevotes.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.Util;

/**
 * Created with IntelliJ IDEA.
 * User: Frank
 * Date: 14/07/13
 * Time: 18:36
 * To change this template use File | Settings | File Templates.
 */
public class SetSignLocation implements Listener {

    private UltimateVotes plugin;
    int blockX;
    int blockY;
    int blockZ;

    public SetSignLocation(UltimateVotes plugin) {
        this.plugin = plugin;
        //plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSetLocation(PlayerInteractEvent e) {

        Player p = e.getPlayer();

        /*if (e.getClickedBlock() != null) {
            if (p.isOp() && p.getItemInHand().getType() == Material.WOODEN_SHOVEL) {

                if (e.getClickedBlock().getBlockData().getAsString().equals("WallSign")) {
                    blockX = e.getClickedBlock().getLocation().getBlockX();
                    blockY = e.getClickedBlock().getLocation().getBlockY();
                    blockZ = e.getClickedBlock().getLocation().getBlockZ();
                    String basePath = "ultimatevotes.signs.sign1.";
                    plugin.getConfig().set(basePath + "x", blockX);
                    plugin.getConfig().set(basePath + "y", blockY);
                    plugin.getConfig().set(basePath + "z", blockZ);
                    plugin.getConfig().set(basePath + "world", p.getWorld().getName());
                    plugin.saveConfig();
                    plugin.reloadConfig();
                    Util.sendMsg(p, ChatColor.GREEN + "Sign set, config saved, and reloaded!");
                } else {
                    Util.sendMsg(p,ChatColor.RED + "You must use hit a sign to set the location.");
                }
            }
        }*/

    }

}
