package net.teozfrank.ultimatevotes.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Copyright teozfrank / FJFreelance 2014 All rights reserved.
 */
public class VExecutor implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        Bukkit.dispatchCommand(sender, "vote sites");
        return true;
    }
}
