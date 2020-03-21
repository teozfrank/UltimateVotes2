package net.teozfrank.ultimatevotes.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import net.teozfrank.ultimatevotes.main.UltimateVotes;

/**
 * Created with IntelliJ IDEA.
 * User: Frank
 * Date: 07/07/13
 * Time: 21:31
 * To change this template use File | Settings | File Templates.
 */
public class SendConsoleMessage {


    private static final String prefix = ChatColor.GREEN + "[UltimateVotes] ";
    private static final String debug = ChatColor.AQUA + "[Debug] ";
    private static final String error = ChatColor.RED + "[Error] ";
    private static final String warning = ChatColor.YELLOW + "[Warning] ";
    private static final String info = "[Info] ";

    public SendConsoleMessage() {

    }

    public static void info(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + info + message);
    }

    public static void error(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + error + message);
    }

    public static void warning(String message) {
        Bukkit.getConsoleSender().sendMessage(prefix + warning + message);
    }

    public static void debug(String message){
        Bukkit.getConsoleSender().sendMessage(prefix + debug + message);
    }

}
