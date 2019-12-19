package net.teozfrank.ultimatevotes.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.Util;

/**
 * Created by Frank on 08/05/2015.
 */
public class VoteRewardsExecutor implements CommandExecutor {

    private UltimateVotes plugin;

    public VoteRewardsExecutor(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(!(sender instanceof Player)) {
            Util.sendMsg(sender, ChatColor.RED + "This command cannot be used as console.");
            return true;
        }

        Player player = (Player) sender;

        if(args.length == 0) {
            player.performCommand("vote rewards");
            return true;
        }

        if(args.length == 1) {
            try {
                int page = Integer.parseInt(args[0]);
                player.performCommand("vote rewards " + page);
                return true;
            } catch (NumberFormatException e) {
                Util.sendMsg(sender, plugin.getMessageManager().getRewardsListNotNumberMessage());
                return true;
            }
        } else {
            Util.sendMsg(sender, plugin.getMessageManager().getUnknownCommandMessage());
            return true;
        }
    }
}
