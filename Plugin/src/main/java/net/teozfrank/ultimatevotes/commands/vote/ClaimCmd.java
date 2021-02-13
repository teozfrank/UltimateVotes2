package net.teozfrank.ultimatevotes.commands.vote;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.*;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

/**
 * Copyright teozfrank / FJFreelance 2014 All rights reserved.
 */
public class ClaimCmd extends VoteCmd {

    public ClaimCmd(UltimateVotes plugin, String mainPerm) {
        super(plugin, mainPerm);
    }

    @Override
    public void run(final CommandSender sender, String subCmd, String[] args) {

        if(!(sender instanceof Player)) {
            Util.sendMsg(sender, NO_CONSOLE);
            return;
        }

        final Player player = (Player) sender;
        final String playerName = player.getName();
        final UUID playerUUID = player.getUniqueId();

        FileManager fm = plugin.getFileManager();
        final RewardsManager rm = plugin.getRewardsManager();
        final MessageManager mm = plugin.getMessageManager();
        VoteManager vm = plugin.getVoteManager();
        final DatabaseManager databaseManager = plugin.getDatabaseManager();

        if(!fm.useClaimCommand()) {
            Util.sendMsg(sender, mm.getClaimDisabledMessage());
            return;
        }

        if(!fm.isRewardsEnabled()) {
            Util.sendEmptyMsg(sender, mm.getRewardDisabledMessage());
            return;
        }

        if(args.length < 1) {

            if(fm.isClaimGUIEnabled()) {
                plugin.getGUIManager().showClaimGUI(player);
                return;
            }

            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                @Override
                public void run() {
                    final int unclaimedVotes = databaseManager.checkUserUnclaimedVotes(playerUUID);

                    plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            List<String> claimCmdMessages = mm.getClaimCmdMessage();//get the list of commands
                            StringBuilder outputMessageToPlayer = new StringBuilder();//get a string builder to store our parsed messages
                            for (String claimMessage : claimCmdMessages) {//for each claim message in the list
                                claimMessage = claimMessage.replaceAll("%unclaimedvotes%", String.valueOf(unclaimedVotes));//replace it with the message variable
                                if (claimMessage.equals("")) {
                                    outputMessageToPlayer.append(" \n");
                                } else {
                                    outputMessageToPlayer.append(claimMessage + "\n");//add the parsed message to the string builder and add a new line
                                }

                            }
                            Util.sendEmptyMsg(sender, outputMessageToPlayer.toString());//send the end result message to the player
                        }
                    });


                }
            });
            return;
        }

        if(args.length == 1) {
            final String value = getValue(args, 0, "");

            if(value.equals("all")) {

                if(rm.hasUnclaimedRewards(playerUUID)) {
                    rm.rewardPlayer(player);
                } else {
                    Util.sendMsg(sender, mm.getNoUnclaimedVotesMessage());
                    return;
                }
            } else {
                try {
                    final int claimAmount = Integer.parseInt(value);

                    if(claimAmount <= 0) {
                        Util.sendMsg(sender, mm.getInvalidClaimAmountMessage());
                        return;
                    }

                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                        @Override
                        public void run() {
                            boolean success = databaseManager.removeUnclaimedVotes(playerUUID, claimAmount);
                            final int unclaimedVotes = databaseManager.checkUserUnclaimedVotes(playerUUID);
                            if(!success) {

                                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        String notEnoughVotesToClaimMessage = mm.getNotEnoughVotesToClaimMessage();
                                        notEnoughVotesToClaimMessage=notEnoughVotesToClaimMessage.replaceAll("%unclaimedvotes%",String.valueOf(unclaimedVotes));
                                        Util.sendMsg(sender,notEnoughVotesToClaimMessage);
                                    }


                                });

                                return;
                            } else {
                                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {

                                    @Override
                                    public void run() {
                                        rm.rewardPlayerByClaimAmount(player, unclaimedVotes, claimAmount);
                                        String claimSuccessMesage = mm.getClaimSuccessMessage();
                                        claimSuccessMesage = claimSuccessMesage.replaceAll("%claimamount%", String.valueOf(claimAmount));
                                        Util.sendMsg(sender, claimSuccessMesage);
                                    }
                                });

                            }
                        }
                    });


                } catch (NumberFormatException ex) {
                    Util.sendMsg(sender, mm.getClaimNotNumberMessage());
                }
            }

        }


        if (vm.hasNotVotedToday.contains(playerName)) {
            vm.hasNotVotedToday.remove(playerName);
            if (plugin.isDebugEnabled()) {
                SendConsoleMessage.debug("adding player " + playerName + " to has voted list.");
            }
            vm.hasVotedToday.put(playerUUID, new Date(System.currentTimeMillis()));
        }

    }
}
