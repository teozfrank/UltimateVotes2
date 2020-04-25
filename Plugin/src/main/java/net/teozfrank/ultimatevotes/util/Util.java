package net.teozfrank.ultimatevotes.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import net.teozfrank.ultimatevotes.main.UltimateVotes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: Frank
 * Date: 15/08/13
 * Time: 00:37
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    private UltimateVotes plugin;

    public Util(UltimateVotes plugin){
        this.plugin = plugin;
    }

    public static String LINE_BREAK = UltimateVotes.getLineBreak();

    public static void sendMsg(CommandSender sender, String message){
        if(message.length() == 0) {
            return;
        }
        sender.sendMessage(UltimateVotes.getPluginPrefix() + message);
    }

    public static void broadcast(String message){
        if(message.length() == 0) {
            return;
        }
        for(Player player: Util.getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    public static void sendMsg(Player player, String message){
        if(message.length() == 0) {
            return;
        }
        if(UltimateVotes.getPluginPrefix().length() == 0) {
            player.sendMessage(message);
            return;
        }
        player.sendMessage(UltimateVotes.getPluginPrefix() + " " + message);
    }

    public static void sendEmptyMsg(CommandSender sender, String message){
        if(message.length() == 0) {
            return;
        }
        if(sender instanceof Player){
            sender.sendMessage(message);
            return;
        }
        sender.sendMessage(message);
    }

    public static void sendPluginCredits(CommandSender sender){
        sendEmptyMsg(sender, LINE_BREAK);
    }

    public static void cmdTitle(CommandSender sender, String title){
        sendEmptyMsg(sender,LINE_BREAK);
        sendEmptyMsg(sender, ChatColor.GOLD + title);
        sendEmptyMsg(sender, "");
    }

    public static void cmdFooter(CommandSender sender, String footer) {
        sendEmptyMsg(sender, "");
        sendEmptyMsg(sender, ChatColor.GOLD + footer);
        sendEmptyMsg(sender, LINE_BREAK);
    }

    public static void sendVoteCountMessage(CommandSender sender,String message) {
        sendEmptyMsg(sender,"");
        sendEmptyMsg(sender, message);
        sendEmptyMsg(sender,"");
    }

    public static List<Player> getOnlinePlayers() {
        List<Player> list = Lists.newArrayList();
        for (World world : Bukkit.getWorlds()) {
            list.addAll(world.getPlayers());
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Asks spigot for the version
     */
    public static String getSpigotVersion() {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=516").openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("GET");
            String version = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
            return version;
        } catch (Exception ex) {
            SendConsoleMessage.error("Could not check for updates for the plugin :(");
        }
        return null;
    }

    public static UUID getOfflineUUID(String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8));
    }

    public static void printList(CommandSender sender, List<String> stringList) {
        for(String string: stringList) {
            sendEmptyMsg(sender, string);
        }
    }

    public static void printList(Player player, List<String> stringList) {
        for(String string: stringList) {
            sendEmptyMsg(player, string);
        }
    }

    public static ItemStack getItemStack(Material material, int amount, String title, List<String> lore) {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static void printArray(String[] array) {
        for(String string: array) {
            SendConsoleMessage.debug(string);
        }
    }

    public static void printTimedCmd(TimedCmd timedCmd) {
        SendConsoleMessage.debug("start time: " + timedCmd.getStartTime());
        SendConsoleMessage.debug("duration: " + timedCmd.getDuration());
        SendConsoleMessage.debug("Commands:");
        Util.printArray(timedCmd.getCmds());
    }
}
