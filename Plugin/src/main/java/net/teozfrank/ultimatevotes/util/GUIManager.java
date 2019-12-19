package net.teozfrank.ultimatevotes.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Frank on 18/01/2016.
 */
public class GUIManager implements Listener {

    private UltimateVotes plugin;
    private HashMap<UUID, Inventory> playerClaimGUIs;

    public GUIManager(UltimateVotes plugin) {
        this.plugin = plugin;
        this.playerClaimGUIs = new HashMap<UUID, Inventory>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void showClaimGUI(Player player) {
        UUID playerUUID = player.getUniqueId();
        Inventory gui;
        gui = playerClaimGUIs.get(playerUUID);

        if(gui == null) {
            Inventory claimGUI = getPlayerClaimGUI();
            this.playerClaimGUIs.put(playerUUID, claimGUI);
            player.openInventory(claimGUI);
        }
        player.openInventory(playerClaimGUIs.get(playerUUID));
    }

    public Inventory getPlayerClaimGUI() {

        Inventory gui = Bukkit.getServer().createInventory(null, 9, plugin.getMessageManager().getClaimGUITitle());
        //ItemStack glass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE, 1);
        //TODO find better way to get item stack between versions for glass
        ItemStack close = Util.getItemStack(Material.BARRIER, 1, ChatColor.AQUA + "Close Inventory", Arrays.asList("Close the claiming inventory"));
        ItemStack claim1 = Util.getItemStack(Material.DIAMOND, 1, ChatColor.AQUA + "Claim 1 Reward", Arrays.asList("Claim one reward"));
        ItemStack claim2 = Util.getItemStack(Material.DIAMOND, 2, ChatColor.AQUA + "Claim 2 Rewards", Arrays.asList("Claim two rewards"));
        ItemStack claim3 = Util.getItemStack(Material.DIAMOND, 3, ChatColor.AQUA + "Claim 3 Rewards", Arrays.asList("Claim three rewards"));
        ItemStack claim5 = Util.getItemStack(Material.DIAMOND, 5, ChatColor.AQUA + "Claim 5 Rewards", Arrays.asList("Claim five rewards"));
        ItemStack claimAll = Util.getItemStack(Material.DIAMOND, 0, ChatColor.AQUA + "Claim all Rewards", Arrays.asList("Claim all rewards"));

        //gui.setItem(0, glass);
        gui.setItem(1, claim1);
        gui.setItem(2, claim2);
        gui.setItem(3, claim3);
        gui.setItem(4, claim5);
        //gui.setItem(5, glass);
        gui.setItem(6, claimAll);
        //gui.setItem(7, glass);
        gui.setItem(8, close);

        return gui;
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerClickGUI(InventoryClickEvent e) {


        if(e.getView().getTitle() == null || e.getInventory() == null) {
            return;
        }

        String claimGUItitle = plugin.getMessageManager().getClaimGUITitle();
        claimGUItitle = ChatColor.stripColor(claimGUItitle);
        if(! e.getView().getTitle().contains(claimGUItitle)) {
            return;
        }

        Player clicker = (Player) e.getWhoClicked();


        e.setCancelled(true);//stop players from stealing the item

        if(e.getCurrentItem() == null) {
            return;
        }

        if(!e.getCurrentItem().hasItemMeta()) {
            return;//if the current item does not have item meta ignore it
        }


        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Claim 1 Reward")) {
            clicker.performCommand("vote claim 1");
            clicker.closeInventory();
            return;
        }

        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Claim 2 Reward")) {
            clicker.performCommand("vote claim 2");
            clicker.closeInventory();
            return;
        }

        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Claim 3 Reward")) {
            clicker.performCommand("vote claim 3");
            clicker.closeInventory();
            return;
        }

        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Claim 5 Reward")) {
            clicker.performCommand("vote claim 5");
            clicker.closeInventory();
            return;
        }

        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Claim All Reward")) {
            clicker.performCommand("vote claim all");
            clicker.closeInventory();
            return;
        }

        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Close Inventory")) {
            clicker.closeInventory();
            return;
        }

    }


}
