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
        String inventoryTitle = plugin.getMessageManager().getClaimGUITitle();
        return plugin.getMaterialHelper().getPlayerClaimGUI(inventoryTitle);
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

        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Claim all Rewards")) {
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
