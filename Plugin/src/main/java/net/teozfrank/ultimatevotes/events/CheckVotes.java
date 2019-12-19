package net.teozfrank.ultimatevotes.events;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import net.teozfrank.ultimatevotes.main.UltimateVotes;

/**
 * Created with IntelliJ IDEA.
 * User: Frank
 * Date: 14/07/13
 * Time: 18:59
 * To change this template use File | Settings | File Templates.
 */
public class CheckVotes implements Listener {

    private UltimateVotes plugin;
    int blockX;
    int blockY;
    int blockZ;
    String world;

    public CheckVotes(UltimateVotes plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSetLocation(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null) {
            Player p = e.getPlayer();
            Block block = e.getClickedBlock();
            String basePath = "ultimatevotes.signs.sign1.";
            blockX = plugin.getConfig().getInt(basePath + "x");
            blockY = plugin.getConfig().getInt(basePath + "y");
            blockZ = plugin.getConfig().getInt(basePath + "z");
            world = plugin.getConfig().getString(basePath + "world");
            if(world == null) {
                return;
            }
            World w = Bukkit.getWorld(world);

            Location loc = new Location(w, blockX, blockY, blockZ);

            /*if (block.getBlockData().getAsString().equals("WallSign") && block.getLocation().equals(loc)) {
                Sign sign = (Sign) block.getState();
                if (plugin.getVoteManager().monthlyVotes.containsKey(p.getName())) {
                    sign.setLine(0, ChatColor.DARK_BLUE + p.getName());
                    sign.setLine(1, ChatColor.GREEN + "has");
                    sign.setLine(2, ChatColor.GOLD + String.valueOf(plugin.getVoteManager().monthlyVotes.get(p.getName())) + ChatColor.GREEN + " Votes");
                    sign.setLine(3, ChatColor.GREEN + "This month :)");
                    sign.update();
                } else {
                    sign.setLine(0, ChatColor.DARK_BLUE + p.getName());
                    sign.setLine(1, ChatColor.GREEN + "is not");
                    sign.setLine(2, ChatColor.GREEN + "in the top");
                    sign.setLine(3, ChatColor.DARK_BLUE + "10 voters :(");
                    sign.update();
                }
            }*/

            //TODO get better way of getting block data information.

        }


    }

}
