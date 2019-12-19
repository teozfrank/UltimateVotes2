package net.teozfrank.ultimatevotes.util;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionOwner;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.threads.WallSignIntroThread;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SetWallSigns7 {

    /*private UltimateVotes plugin;

    public SetWallSigns7(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    public void setWallSignsLocation(Player player) {
        double pos1x, pos1y, pos1z, pos2x, pos2y, pos2z;
        //String worldName;
        //World selWorld;
        Location pos1;
        Location pos2;
        FileManager fm = plugin.getFileManager();
        SignManager sm = plugin.getSignManager();

        SessionOwner sessionOwner = BukkitAdapter.adapt(player);
        LocalSession playerSession = WorldEdit.getInstance().getSessionManager().get(sessionOwner);

        Region playerSelection;

        try {
            playerSelection = playerSession.getSelection(playerSession.getSelectionWorld());
        } catch (IncompleteRegionException e) {
            Util.sendMsg(player, ChatColor.RED + "Region selection incomplete!");
            return;
        }

        BlockVector3 minimumPoint = playerSelection.getMinimumPoint();
        BlockVector3 maximumPoint = playerSelection.getMaximumPoint();

        World world = Bukkit.getWorld(playerSession.getSelectionWorld().getName());

        pos1x = maximumPoint.getX();
        pos1y = maximumPoint.getY();
        pos1z = maximumPoint.getZ();
        pos1 = new Location(world, pos1x, pos1y, pos1z);


        pos2x = minimumPoint.getX();
        pos2y = minimumPoint.getY();
        pos2z = minimumPoint.getZ();
        pos2 = new Location(world, pos2x, pos2y, pos2z);

        if (! sm.isRegionAllWallSigns(pos1, pos2)) {
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

        fm.saveSigns();
        fm.reloadSigns();
        Util.sendEmptyMsg(player, ChatColor.GREEN + "Successfully set sign wall!");
        plugin.getServer().getScheduler().runTask(plugin, new WallSignIntroThread(plugin));
    }*/
}
