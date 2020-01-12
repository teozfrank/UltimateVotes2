package net.teozfrank.ultimatevotes.worldedit.legacy;

import net.teozfrank.ultimatevotes.api.WorldEditHelper;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.threads.WallSignIntroThread;
import net.teozfrank.ultimatevotes.util.FileManager;
import net.teozfrank.ultimatevotes.util.SignManager;
import net.teozfrank.ultimatevotes.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldEditLegacy implements WorldEditHelper {

    private UltimateVotes plugin;

    public WorldEditLegacy(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setWallSignsLocation(Player player) {
        double pos1x, pos1y, pos1z, pos2x, pos2y, pos2z;
        //String worldName;
        //World selWorld;
        Location pos1;
        Location pos2;
        FileManager fm = plugin.getFileManager();
        SignManager sm = plugin.getSignManager();


        WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        Selection selection = worldEdit.getSelection(player);

        if (selection != null) {
            World world = selection.getWorld();
            Location min = selection.getMinimumPoint();
            Location max = selection.getMaximumPoint();

            pos1x = max.getX();
            pos1y = max.getY();
            pos1z = max.getZ();
            pos1 = new Location(world, pos1x, pos1y, pos1z);


            pos2x = min.getX();
            pos2y = min.getY();
            pos2z = min.getZ();
            pos2 = new Location(world, pos2x, pos2y, pos2z);

            if (!sm.isRegionAllWallSigns(pos1, pos2)) {
                Util.sendMsg(player, ChatColor.RED + "Your sign region selection is not all wall signs or is not 3x3!, please reselect the region!");
                return;
            }

            String basePath = "signs.signwall.";
            fm.getSigns().set(basePath + "pos1.world", world.getName());
            fm.getSigns().set(basePath + "pos1.x", pos1x);
            fm.getSigns().set(basePath + "pos1.y", pos1y);
            fm.getSigns().set(basePath + "pos1.z", pos1z);

            fm.getSigns().set(basePath + "pos2.x", pos2x);
            fm.getSigns().set(basePath + "pos2.y", pos2y);
            fm.getSigns().set(basePath + "pos2.z", pos2z);
            fm.getSigns().set(basePath + "pos2.world", world.getName());

        } else {
            Util.sendMsg(player, ChatColor.RED + "You have not selected a region, please select one first!");
            return;
        }
        fm.saveSigns();
        fm.reloadSigns();
        Util.sendEmptyMsg(player, ChatColor.GREEN + "Successfully set sign wall!");
        plugin.getServer().getScheduler().runTask(plugin, new WallSignIntroThread(plugin));
    }
}
