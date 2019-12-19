package net.teozfrank.ultimatevotes.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.Util;

/**
 * Copyright teozfrank / FJFreelance 2014 All rights reserved.
 */
public class ClaimExecutor implements CommandExecutor {

    private UltimateVotes plugin;

    public ClaimExecutor(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args.length < 1) {
            Bukkit.dispatchCommand(sender, "vote claim");
        }

        if(args.length == 1) {
            String value = args[0];

            if(value.equals("all")) {
                Bukkit.dispatchCommand(sender, "vote claim all");
            } else {
                try {
                    int claimAmount = Integer.parseInt(value);
                    Bukkit.dispatchCommand(sender, "vote claim " + claimAmount);
                } catch (NumberFormatException e) {
                    Util.sendMsg(sender, plugin.getMessageManager().getClaimNotNumberMessage());
                }


            }
        }

        return true;
    }
}
