package net.teozfrank.ultimatevotes.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public interface MaterialHelper {

    boolean isWallSign(Location location);
    boolean isWallSign(Block block);
    Material getGlassPane();


}
