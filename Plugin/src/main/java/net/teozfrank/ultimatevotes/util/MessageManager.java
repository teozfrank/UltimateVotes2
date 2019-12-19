package net.teozfrank.ultimatevotes.util;

import org.bukkit.ChatColor;
import net.teozfrank.ultimatevotes.main.UltimateVotes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank on 22/05/2014.
 */
public class MessageManager {

    private UltimateVotes plugin;
    private FileManager fm;

    public MessageManager(UltimateVotes plugin) {
        this.plugin = plugin;
        this.fm = plugin.getFileManager();
    }


    public List<String> getVoteReminder() {
        return fm.getMessages().getStringList("messages.reminder");
    }

    public List<String> getVoteSites() {
        return fm.getMessages().getStringList("messages.sites");
    }

    public List<String> getJoinMessage() {
        return fm.getMessages().getStringList("messages.joinmessage");
    }

    public String getRewardMessage() {
        String rewardMessage = fm.getMessages().getString("messages.reward.message");
        rewardMessage = ChatColor.translateAlternateColorCodes('&', rewardMessage);
        return rewardMessage;
    }

    public String getRewardDisabledMessage() {
        String rewardDisabledMessage = fm.getMessages().getString("messages.reward.disabled");
        rewardDisabledMessage = ChatColor.translateAlternateColorCodes('&', rewardDisabledMessage);
        return rewardDisabledMessage;
    }

    public String getClaimMessage() {
        String claimMessage = fm.getMessages().getString("messages.claim.message");
        claimMessage = ChatColor.translateAlternateColorCodes('&', claimMessage);
        return claimMessage;
    }

    public String getClaimDisabledMessage() {
        String claimDisabledMessage = fm.getMessages().getString("messages.claim.disabled");
        claimDisabledMessage = ChatColor.translateAlternateColorCodes('&', claimDisabledMessage);
        return claimDisabledMessage;
    }


    public String getSignWallTitle1() {
        String signWallTitle1 = fm.getMessages().getString("messages.signwall.title.1");
        signWallTitle1 = ChatColor.translateAlternateColorCodes('&', signWallTitle1);
        return signWallTitle1;
    }

    public String getSignWallTitle2() {
        String signWallTitle2 = fm.getMessages().getString("messages.signwall.title.2");
        signWallTitle2 = ChatColor.translateAlternateColorCodes('&', signWallTitle2);
        return signWallTitle2;
    }

    public String getSignWallLastUpdated1() {
        String signWallLastUpdated1 = fm.getMessages().getString("messages.signwall.lastupdated.1");
        signWallLastUpdated1 = ChatColor.translateAlternateColorCodes('&', signWallLastUpdated1);
        return signWallLastUpdated1;
    }

    public String getSignWallLastUpdated2() {
        String signWallLastUpdated2 = fm.getMessages().getString("messages.signwall.lastupdated.2");
        signWallLastUpdated2 = ChatColor.translateAlternateColorCodes('&', signWallLastUpdated2);
        return signWallLastUpdated2;
    }

    public String getLineBreak() {
        String lineBreak = fm.getMessages().getString("messages.linebreak");
        lineBreak = ChatColor.translateAlternateColorCodes('&', lineBreak);
        return lineBreak;
    }

    public String getVoteTopTitle() {
        String voteTopTitle = fm.getMessages().getString("messages.top.title");
        voteTopTitle = ChatColor.translateAlternateColorCodes('&', voteTopTitle);
        return voteTopTitle;
    }

    public String getMVoteTopTitle() {
        String mVoteTopTitle = fm.getMessages().getString("messages.mtop.title");
        mVoteTopTitle = ChatColor.translateAlternateColorCodes('&', mVoteTopTitle);
        return mVoteTopTitle;
    }

    public String getVoteAllTimeTitle() {
        String getVoteAllTimeTitle = fm.getMessages().getString("messages.alltime.title");
        getVoteAllTimeTitle = ChatColor.translateAlternateColorCodes('&', getVoteAllTimeTitle);
        return getVoteAllTimeTitle;
    }

    public String getMVoteAllTimeTitle() {
        String mVoteAllTimeTitle = fm.getMessages().getString("messages.malltime.title");
        mVoteAllTimeTitle = ChatColor.translateAlternateColorCodes('&', mVoteAllTimeTitle);
        return mVoteAllTimeTitle;
    }

    public String getLastUpdatedFormat() {
        String lastUpdatedFormat = fm.getMessages().getString("messages.lastupdatedformat");
        lastUpdatedFormat = ChatColor.translateAlternateColorCodes('&', lastUpdatedFormat);
        return lastUpdatedFormat;
    }

    public String getLastUpdatedFormatted(String minutes, String seconds) {
        String lastUpdatedFormatted = getLastUpdatedFormat();
        lastUpdatedFormatted = lastUpdatedFormatted.replaceAll("%mins%", minutes);
        lastUpdatedFormatted = lastUpdatedFormatted.replaceAll("%seconds%", seconds);
        return lastUpdatedFormatted;
    }

    public List<String> getVoteCommandList() {
        List<String> voteCmdList = fm.getMessages().getStringList("messages.vote.commandlist");
        List<String> parsedCmdList = new ArrayList<String>();
        for(String cmd: voteCmdList) {
            cmd = ChatColor.translateAlternateColorCodes('&', cmd);
            parsedCmdList.add(cmd);
        }
        return parsedCmdList;
    }

    public String getVoteCommandListTitle() {
        String voteCommnadListTitle = fm.getMessages().getString("messages.vote.title");
        voteCommnadListTitle = ChatColor.translateAlternateColorCodes('&', voteCommnadListTitle);
        return voteCommnadListTitle;
    }

    public String getRewardListTile() {
        String voteCommnadListTitle = fm.getMessages().getString("messages.rewardlist.title");
        voteCommnadListTitle = ChatColor.translateAlternateColorCodes('&', voteCommnadListTitle);
        return voteCommnadListTitle;
    }

    public String getRewardListFooter() {
        String voteCommnadListFooter = fm.getMessages().getString("messages.rewardlist.footer");
        voteCommnadListFooter = ChatColor.translateAlternateColorCodes('&', voteCommnadListFooter);
        return voteCommnadListFooter;
    }

    /**
     * check to see does the reward list have a next page
     * @param page the current page number
     * @return true if has a next page false if not
     */
    public boolean hasNextPage(int page) {
        page = (page+1);
        if(fm.getMessages().isSet("messages.rewardlist.pages." + page)) {
            return true;
        }
        return false;
    }

    /**
     * check to see does the reward list have the current page number
     * @param page the current page number
     * @return true if has the current page, false if not
     */
    public boolean hasPageNumber(int page) {
        if(fm.getMessages().isSet("messages.rewardlist.pages." + page)) {
            return true;
        }
        return false;
    }

    /**
     * get the list of rewards shown to players
     * when /vote rewards is ran
     * @return a list of parsed rewards
     * @param page the page number to retrieve
     */
    public List<String> getParsedRewardsList(int page) {
        List<String> rewardList = fm.getMessages().getStringList("messages.rewardlist.pages." + page);
        if(plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("reward list page " + page + " : " + rewardList.toString());
        }
        List<String> parsedRewardList = new ArrayList<String>();
        for(String cmd: rewardList) {
            cmd = ChatColor.translateAlternateColorCodes('&', cmd);
            parsedRewardList.add(cmd);
        }
        return parsedRewardList;
    }

    /**
     * get the vote broadcast message
     * used in non bungeecord servers
     * @return the vote broadcast message
     */
    public String getVoteBroadcastMessage() {
        String voteAnnouncement = fm.getMessages().getString("messages.announcement");
        voteAnnouncement = ChatColor.translateAlternateColorCodes('&', voteAnnouncement);
        return voteAnnouncement;
    }

    /**
     * get the mtop cmd message sent when a player uses /vote mtop
     * @return the message
     */
    public String getMTopCmdMessage() {
        String mTopCmdMessage = fm.getMessages().getString("messages.mtop.message");
        mTopCmdMessage = ChatColor.translateAlternateColorCodes('&', mTopCmdMessage);
        return mTopCmdMessage;
    }

    /**
     * get the malltime cmd message sent when a player uses /vote malltime
     * @return the message
     */
    public String getMAllTimeCmdMessage() {
        String mAllTimeCmdMessage = fm.getMessages().getString("messages.malltime.message");
        mAllTimeCmdMessage = ChatColor.translateAlternateColorCodes('&', mAllTimeCmdMessage);
        return mAllTimeCmdMessage;
    }

    /**
     * get a list of claim messages to be displayed
     * when the player uses the /claim command
     * @return a list of messages to be displayed
     */
    public List<String> getClaimCmdMessage() {
        List<String> claimCmdMessage = fm.getMessages().getStringList("messages.claim.commandlist");

        List<String> parsedClaimCmdList = new ArrayList<String>();
        for(String cmd: claimCmdMessage) {
            cmd = ChatColor.translateAlternateColorCodes('&', cmd);
            parsedClaimCmdList.add(cmd);
        }
        return parsedClaimCmdList;
    }

    /**
     * get the message returned to the player when they have successfully
     * claimed a reward
     * @return the message to show the player
     */
    public String getClaimSuccessMessage() {
        String claimSuccessMessage = fm.getMessages().getString("messages.claim.success");
        claimSuccessMessage = ChatColor.translateAlternateColorCodes('&', claimSuccessMessage);
        return claimSuccessMessage;
    }

    /**
     * get the top list format message
     * @return the format of which to display the top list
     */
    public String getTopListFormatMessage() {
        String topListFormatMessage = fm.getMessages().getString("messages.toplistformat");
        topListFormatMessage = ChatColor.translateAlternateColorCodes('&', topListFormatMessage);
        return topListFormatMessage;
    }

    /**
     * get the message returned to the player when they try to claim
     * and do not have any unclaimed rewards
     * @return the message to show the player
     */
    public String getNoUnclaimedVotesMessage() {
        String noUnclaimedVotesMessage = fm.getMessages().getString("messages.claim.nounclaimedvotes");
        noUnclaimedVotesMessage = ChatColor.translateAlternateColorCodes('&', noUnclaimedVotesMessage);
        return noUnclaimedVotesMessage;
    }

    /**
     * get the message returned to the player when they try to claim
     * and do not have the required amount of votes to claim that much
     * @return the message to show the player
     */
    public String getNotEnoughVotesToClaimMessage() {
        String noteEnoughVotesToClaim = fm.getMessages().getString("messages.claim.notenoughvotes");
        noteEnoughVotesToClaim = ChatColor.translateAlternateColorCodes('&', noteEnoughVotesToClaim);
        return noteEnoughVotesToClaim;
    }

    /**
     * get the message returned to the player when they try to claim
     * and do not have the required amount of votes to claim that much
     * @return the message to show the player
     */
    public String getInvalidClaimAmountMessage() {
        String invalidClaimAmountMessage = fm.getMessages().getString("messages.claim.invalidamount");
        invalidClaimAmountMessage = ChatColor.translateAlternateColorCodes('&', invalidClaimAmountMessage);
        return invalidClaimAmountMessage;
    }

    /**
     * get the message returned to the player when they try to claim
     * and do not provide a claim amount or the text 'all'
     * @return the message to show the player
     */
    public String getClaimNotNumberMessage() {
        String claimNotNumberMessage = fm.getMessages().getString("messages.claim.notnumber");
        claimNotNumberMessage = ChatColor.translateAlternateColorCodes('&', claimNotNumberMessage);
        return claimNotNumberMessage;
    }

    /**
     * get the title of the claim gui when shown to players
     * @return the string title of the claim gui
     */
    public String getClaimGUITitle() {
        String claimGUITitle = fm.getMessages().getString("messages.claimgui.title");
        claimGUITitle = ChatColor.translateAlternateColorCodes('&',  claimGUITitle);
        return claimGUITitle;
    }

    /**
     * Get the message shown to users when they enter an incorrect sub command for a command
     * @return the string message to be shown
     */
    public String getSubCommandNotValidMessage() {
        String notValidCommandMessage = fm.getMessages().getString("messages.errors.subcmdnotvalid");
        notValidCommandMessage = ChatColor.translateAlternateColorCodes('&',  notValidCommandMessage);
        return notValidCommandMessage;
    }

    /**
     * Get the message shown to users when they enter an incorrect sub command for a command
     * @return the string message to be shown
     */
    public String getRewardsListNotNumberMessage() {
        String notANumberMessage = fm.getMessages().getString("messages.rewardlist.notnumber");
        notANumberMessage = ChatColor.translateAlternateColorCodes('&',  notANumberMessage);
        return notANumberMessage;
    }

    /**
     * Get the message shown to users when they enter an incorrect sub command for a command
     * @return the string message to be shown
     */
    public String getRewardsListLastPageMessage() {
        String lastPageMessage = fm.getMessages().getString("messages.rewardlist.lastpage");
        lastPageMessage = ChatColor.translateAlternateColorCodes('&',  lastPageMessage);
        return lastPageMessage;
    }

    /**
     * Get the message shown to users when they enter an unknown command
     * @return the string message to be shown
     */
    public String getUnknownCommandMessage() {
        String unknownCmdMessage = fm.getMessages().getString("messages.errors.unknowncmd");
        unknownCmdMessage = ChatColor.translateAlternateColorCodes('&',  unknownCmdMessage);
        return unknownCmdMessage;
    }

    /**
     * Check is the monthly reset for the monthlyvotes table enabled
     * @return true if enabled, false if not
     */
    public boolean isMonthlyResetEnabled() {
        boolean isMonthlyResetEnabled = plugin.getConfig().getBoolean("ultimatevotes.votes.autoresetmonthlyvotes");
        return isMonthlyResetEnabled;
    }

}
