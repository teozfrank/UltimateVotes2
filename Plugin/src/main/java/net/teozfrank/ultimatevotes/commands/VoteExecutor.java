package net.teozfrank.ultimatevotes.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.teozfrank.ultimatevotes.commands.vote.*;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.Util;

/**
 * Created by frank on 12/07/2014.
 */
public class VoteExecutor extends CmdExecutor implements CommandExecutor {

    public VoteExecutor(UltimateVotes plugin) {
        super(plugin);
        VoteCmd top = new TopCmd(plugin, "ultimatevotes.player.top");
        VoteCmd allTime = new AllTimeCmd(plugin, "ultimatevotes.player.alltime");
        VoteCmd mTop = new MTopCmd(plugin, "ultimatevotes.player.mytop");
        VoteCmd mAllTime = new MAllTimeCmd(plugin, "ultimatevotes.player.myalltime");
        VoteCmd sites = new SitesCmd(plugin, "ultimatevotes.player.sites");
        VoteCmd about = new AboutCmd(plugin, "ultimatevotes.player.info");
        VoteCmd claim = new ClaimCmd(plugin, "ultimatevotes.player.claim");
        VoteCmd rewards = new RewardsCmd(plugin, "ultimatevotes.player.rewards");

        addCmd("top", top, new String[]{
                "t","monthly"
        });

        addCmd("alltime", allTime, new String[]{
                "at","overall"
        });

        addCmd("mtop", mTop, new String[]{
                "mytop","mt"
        });

        addCmd("malltime", mAllTime, new String[]{
                "myalltime","mat"
        });

        addCmd("sites", sites, new String[]{
                "websites","s","w"
        });

        addCmd("about", about, new String[]{
                "author","developer"
        });

        addCmd("claim", claim, new String[]{
                "c","cl"
        });

        addCmd("rewards", rewards, new String[]{
                "r"
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (command.getName().equalsIgnoreCase("vote")) {

            if (args.length < 1) {
                Util.sendEmptyMsg(sender,Util.LINE_BREAK);
                Util.sendEmptyMsg(sender,"");
                Util.sendEmptyMsg(sender, plugin.getMessageManager().getVoteCommandListTitle());
                Util.sendEmptyMsg(sender,"");
                Util.printList(sender, plugin.getMessageManager().getVoteCommandList());
                Util.sendEmptyMsg(sender,"");
                Util.sendPluginCredits(sender);
                return true;
            }

            String sub = args[0].toLowerCase();

            VoteCmd cmd = (VoteCmd) super.getCmd(sub);

            if (cmd == null) {
                String subCmdNotValidMessage = plugin.getMessageManager().getSubCommandNotValidMessage();
                subCmdNotValidMessage = subCmdNotValidMessage.replaceAll("%subcmd%", sub);
                subCmdNotValidMessage = subCmdNotValidMessage.replaceAll("%cmd%", "vote");

                Util.sendMsg(sender, subCmdNotValidMessage);
                return true;
            }

            sub = cmd.getCommand(sub);

            if (sender instanceof Player) {
                Player p = (Player) sender;

                if (! p.hasPermission(cmd.permission)) {
                    Util.sendMsg(p, ChatColor.RED + "Sorry but you do not have permission for this command");
                    return true;
                }
            }

            try {
                cmd.run(sender, sub, makeParams(args, 1));
            } catch (ArrayIndexOutOfBoundsException e) {
                Util.sendMsg(sender, ChatColor.RED + "You entered invalid parameters for this command!.");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Util.sendMsg(sender, cmd.GEN_ERROR);
                return true;
            }

            return true;

        }

        return false;
    }

}
