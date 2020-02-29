package net.teozfrank.ultimatevotes.materialhelper.legacy;

import net.teozfrank.ultimatevotes.api.MaterialHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class MaterialHelperLegacy implements MaterialHelper {



    @Override
    public boolean isWallSign(Block block) {
        if(block.getType() == Material.WALL_SIGN) {
            return true;
        }
        return false;
    }

    @Override
    public Material getGlassPane() {
        return Material.STAINED_GLASS_PANE;
    }

    @Override
    public Inventory getPlayerClaimGUI(String title) {
        Inventory gui = Bukkit.getServer().createInventory(null, 9, title);
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1);
        ItemStack close = getItemStack(Material.BARRIER, 1, ChatColor.AQUA + "Close Inventory", Arrays.asList("Close the claiming inventory"));
        ItemStack claim1 = getItemStack(Material.DIAMOND, 1, ChatColor.AQUA + "Claim 1 Reward", Arrays.asList("Claim one reward"));
        ItemStack claim2 = getItemStack(Material.DIAMOND, 2, ChatColor.AQUA + "Claim 2 Rewards", Arrays.asList("Claim two rewards"));
        ItemStack claim3 = getItemStack(Material.DIAMOND, 3, ChatColor.AQUA + "Claim 3 Rewards", Arrays.asList("Claim three rewards"));
        ItemStack claim5 = getItemStack(Material.DIAMOND, 5, ChatColor.AQUA + "Claim 5 Rewards", Arrays.asList("Claim five rewards"));
        ItemStack claimAll = getItemStack(Material.DIAMOND, 0, ChatColor.AQUA + "Claim all Rewards", Arrays.asList("Claim all rewards"));

        gui.setItem(0, glass);
        gui.setItem(1, claim1);
        gui.setItem(2, claim2);
        gui.setItem(3, claim3);
        gui.setItem(4, claim5);
        gui.setItem(5, glass);
        gui.setItem(6, claimAll);
        gui.setItem(7, glass);
        gui.setItem(8, close);

        return gui;
    }

    @Override
    public ItemStack getItemStack(Material material, int amount, String title, List<String> lore) {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
