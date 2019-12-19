package net.teozfrank.ultimatevotes.commands.vote;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.SendConsoleMessage;
import net.teozfrank.ultimatevotes.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Frank on 08/05/2015.
 */
public class RewardsCmd extends VoteCmd {


    public RewardsCmd(UltimateVotes plugin, String mainPerm) {
        super(plugin, mainPerm);
    }

    @Override
    public void run(CommandSender sender, String subCmd, String[] args) {
        List<String> rewardList = new ArrayList<String>();
        int page = 1;
        if(args.length == 0 ) {
            rewardList = plugin.getMessageManager().getParsedRewardsList(1);
        }

        try {
            int pageIn = Integer.parseInt(getValue(args, 0, "1"));
            page = pageIn;
            if(!plugin.getMessageManager().hasPageNumber(page)) {
                Util.sendMsg(sender, ChatColor.RED + "This page does not exist!");
                return;
            }
            rewardList = plugin.getMessageManager().getParsedRewardsList(page);
        } catch (NumberFormatException e) {
            Util.sendMsg(sender, plugin.getMessageManager().getRewardsListNotNumberMessage());
            return;
        }

        StringBuilder rewardListOut = new StringBuilder();
        for(String reward: rewardList) {
            rewardListOut.append(reward + "\n");
        }
        String title = plugin.getMessageManager().getRewardListTile();
        title = title.replaceAll("%page%", String.valueOf(page));
        Util.cmdTitle(sender, title);
        Util.sendEmptyMsg(sender, rewardListOut.toString());
        String footer = plugin.getMessageManager().getRewardListFooter();
        footer = footer.replaceAll("%nextpage%", String.valueOf(page + 1));
        int footerLength = footer.length();
        if(plugin.getMessageManager().hasNextPage(page)) {
            if(footerLength > 0) {
                Util.cmdFooter(sender, footer);
            }
        } else {
            Util.cmdFooter(sender, plugin.getMessageManager().getRewardsListLastPageMessage());
        }
    }
}
