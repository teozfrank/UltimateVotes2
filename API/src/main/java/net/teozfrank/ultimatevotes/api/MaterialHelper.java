package net.teozfrank.ultimatevotes.api;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface MaterialHelper {

    boolean isWallSign(Block block);
    Material getGlassPane();
    Inventory getPlayerClaimGUI(String title);
    ItemStack getItemStack(Material material, int amount, String title, List<String> lore);

}
