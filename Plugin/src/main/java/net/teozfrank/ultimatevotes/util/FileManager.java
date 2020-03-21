package net.teozfrank.ultimatevotes.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.threads.WallSignIntroThread;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Frank
 * Date: 10/08/13
 * Time: 19:57
 * To change this template use File | Settings | File Templates.
 */
public class FileManager {

    private UltimateVotes plugin;

    public FileManager(UltimateVotes plugin) {
        this.plugin = plugin;
    }


    private FileConfiguration rewards = null;
    private FileConfiguration topVoters = null;
    private FileConfiguration messages = null;
    private FileConfiguration signs = null;
    private File rewardsFile = null;
    private File topVotersFile = null;
    private File messagesFile = null;
    private File signsFile = null;


    public void reloadRewards() {
        if (rewardsFile == null) {
            rewardsFile = new File(plugin.getDataFolder(), "rewards.yml");
        }
        rewards = YamlConfiguration.loadConfiguration(rewardsFile);

        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(rewardsFile);
        rewards.setDefaults(defConfig);

    }

    public FileConfiguration getRewards() {
        if (rewards == null) {
            this.reloadRewards();
        }
        return rewards;
    }

    public void saveRewards() {
        if (rewards == null || rewardsFile == null) {
            return;
        }
        try {
            this.getRewards().save(rewardsFile);
        } catch (IOException e) {
            SendConsoleMessage.error("Error saving rewards config!");
        }
    }

    public void saveDefaultRewards() {
        if (rewardsFile == null) {
            rewardsFile = new File(plugin.getDataFolder(), "rewards.yml");
        }
        if (!rewardsFile.exists()) {
            plugin.saveResource("rewards.yml", false);
        }
    }

    public boolean hasVoteReward(int voteAmount) {
        if (getRewards().isSet("rewards." + voteAmount)) {
            return true;
        }
        return false;
    }

    public boolean hasVoteRewardByWorld(String world, int voteAmount) {
        if (getRewards().isSet("rewards." + world + "." + voteAmount)) {
            return true;
        }
        return false;
    }

    /**
     * method to load rewards into cache, not currently needed, not complete, but may be useful in the future
     */
    public void loadRewards() {

        RewardsManager rm = plugin.getRewardsManager();

        ConfigurationSection section = this.getRewards().getConfigurationSection("rewards");

        if (section == null) {
            return;
        }

        Set<String> rewardsSet = section.getKeys(false);

        if (rewardsSet != null) {
            for (String aReward : rewardsSet) {
                SendConsoleMessage.debug(aReward);

                String path = "rewards." + aReward;

                try {
                    int aRewardInt = Integer.parseInt(aReward);
                    List<String> commands = this.getRewards().getStringList(path);
                    if (plugin.isDebugEnabled()) {
                        for (String command : commands) {
                            SendConsoleMessage.debug(command);
                        }
                    }
                    //rm.addReward(aRewardInt, commands);
                } catch (Exception e) {
                    SendConsoleMessage.warning("Could not load reward at index " + aReward);
                }


            }
            SendConsoleMessage.info("Rewards Loaded!");

        }

    }

    public Location getWallSignsPos1() {
        String basePath = "signs.signwall.pos1.";
        String worldName = getSigns().getString(basePath + "world");
        if (worldName == null) {
            return null;
        }
        World world = Bukkit.getWorld(worldName);
        double x = getSigns().getDouble(basePath + "x");
        double y = getSigns().getDouble(basePath + "y");
        double z = getSigns().getDouble(basePath + "z");

        if (plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Pos1: (" + x + ")  (" + y + ") (" + z + ") World:" + worldName);
        }

        if (world != null) {
            return new Location(world, x, y, z);
        }
        return null;

    }

    public Location getWallSignsPos2() {
        String basePath = "signs.signwall.pos2.";
        String worldName = getSigns().getString(basePath + "world");
        if (worldName == null) {
            return null;
        }
        World world = Bukkit.getWorld(worldName);
        double x = getSigns().getDouble(basePath + "x");
        double y = getSigns().getDouble(basePath + "y");
        double z = getSigns().getDouble(basePath + "z");

        if (plugin.isDebugEnabled()) {
            SendConsoleMessage.debug("Pos2: (" + x + ")  (" + y + ") (" + z + ") World:" + worldName);
        }

        if (world != null) {
            return new Location(world, x, y, z);
        }
        return null;

    }

    public void reloadTopVoters() {
        if (topVotersFile == null) {
            topVotersFile = new File(plugin.getDataFolder(), "topvoters.yml");
        }
        topVoters = YamlConfiguration.loadConfiguration(topVotersFile);

        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(topVotersFile);
        topVoters.setDefaults(defConfig);


    }

    public FileConfiguration getTopVoters() {
        if (topVoters == null) {
            this.reloadTopVoters();
        }
        return topVoters;
    }

    public void saveTopVoters() {
        if (topVoters == null || topVotersFile == null) {
            return;
        }
        try {
            this.getTopVoters().save(topVotersFile);
        } catch (IOException e) {
            SendConsoleMessage.error("Error saving top voters!");
        }
    }

    public void saveDefaultTopVoters() {
        if (topVotersFile == null) {
            topVotersFile = new File(plugin.getDataFolder(), "topvoters.yml");
        }
        if (!topVotersFile.exists()) {
            plugin.saveResource("topvoters.yml", false);
        }
    }

    public void reloadMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);

        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(messagesFile);
        messages.setDefaults(defConfig);

    }

    public FileConfiguration getMessages() {
        if (messages == null) {
            this.reloadMessages();
        }
        return messages;
    }

    public void saveMessages() {
        if (messages == null || messagesFile == null) {
            return;
        }
        try {
            this.getMessages().save(messagesFile);
        } catch (IOException e) {
            SendConsoleMessage.error("Error saving messages!");
        }
    }

    public void saveDefaultMessages() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    public void reloadSigns() {
        if (signsFile == null) {
            signsFile = new File(plugin.getDataFolder(), "signs.yml");
        }
        signs = YamlConfiguration.loadConfiguration(signsFile);

        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(signsFile);
        signs.setDefaults(defConfig);

    }

    public FileConfiguration getSigns() {
        if (signs == null) {
            this.reloadSigns();
        }
        return signs;
    }

    public void saveSigns() {
        if (signs == null || signsFile == null) {
            return;
        }
        try {
            this.getSigns().save(signsFile);
        } catch (IOException e) {
            SendConsoleMessage.error("Error saving signs!");
        }
    }

    public void saveDefaultSigns() {
        if (signsFile == null) {
            signsFile = new File(plugin.getDataFolder(), "signs.yml");
        }
        if (!signsFile.exists()) {
            plugin.saveResource("signs.yml", false);
        }
    }

    public boolean isMySqlEnabled() {
        boolean isMySqlEnabled = plugin.getConfig().getBoolean("ultimatevotes.mysql.enabled");
        return isMySqlEnabled;
    }

    public String getSignWallLayout() {
        String signWallLayout = plugin.getFileManager().getMessages().getString("messages.signwall.topvoterslayout");
        signWallLayout = ChatColor.translateAlternateColorCodes('&', signWallLayout);
        return signWallLayout;
    }

    public boolean setWallSignsLocation(Player player, Location pos1, Location pos2) {

        try {
            String basePath = "signs.signwall.";
            this.getSigns().set(basePath + "pos1.world", pos1.getWorld().getName());
            this.getSigns().set(basePath + "pos1.x", pos1.getBlockX());
            this.getSigns().set(basePath + "pos1.y", pos1.getBlockY());
            this.getSigns().set(basePath + "pos1.z", pos1.getBlockZ());

            this.getSigns().set(basePath + "pos2.x", pos2.getBlockX());
            this.getSigns().set(basePath + "pos2.y", pos2.getBlockY());
            this.getSigns().set(basePath + "pos2.z", pos2.getBlockZ());
            this.getSigns().set(basePath + "pos2.world", pos2.getWorld().getName());

            this.saveSigns();
            this.reloadSigns();
            Util.sendEmptyMsg(player, ChatColor.GREEN + "Successfully saved wall sign location!");
            plugin.getServer().getScheduler().runTask(plugin, new WallSignIntroThread(plugin));
            return true;
        } catch (Exception ex) {
            Util.sendEmptyMsg(player, ChatColor.GREEN + "Saving wall sign location failed see console for error!");
            SendConsoleMessage.error("Saving sign location failed: " + ex.getMessage());
            return false;
        }
    }


    public boolean useClaimCommand() {
        return plugin.getConfig().getBoolean("ultimatevotes.rewards.claiming.useclaimcommand");
    }

    public boolean isTopVotersLogEnabled() {
        return plugin.getConfig().getBoolean("ultimatevotes.topvoterslog.enabled");
    }

    public int getTopVotersLogLimit() {
        return plugin.getConfig().getInt("ultimatevotes.topvoterslog.limit");
    }

    public boolean rewardByMonthlyVotesCount() {
        return plugin.getConfig().getBoolean("ultimatevotes.rewards.cumulativerewards.rewardbymonthlyvotescount");
    }

    public String getClaimMessage() {
        String claimMessage = plugin.getConfig().getString("ultimatevotes.votes.claim.claimmessage");
        claimMessage = ChatColor.translateAlternateColorCodes('&', claimMessage);
        return claimMessage;
    }

    public boolean rewardByWorld() {
        return plugin.getConfig().getBoolean("ultimatevotes.rewards.rewardsbyworld");
    }

    /**
     * should we reward players when they are online when claiming is enabled?
     * @return true to reward players when online and claiming enabled, false if not
     */
    public boolean rewardOnline() {
        return plugin.getConfig().getBoolean("ultimatevotes.rewards.claiming.rewardonline");
    }

    public boolean isUsingBungeeCord() {
        return plugin.getConfig().getBoolean("ultimatevotes.votes.usingbungeecord");
    }

    public boolean isUpdateCheckEnabled() {
        return plugin.getConfig().getBoolean("ultimatevotes.checkforupdates");
    }

    public boolean isRewardsEnabled() {
        return plugin.getConfig().getBoolean("ultimatevotes.rewards.enabled");
    }

    public int getDailyVoteTarget() {
        return getRewards().getInt("dailytarget.votes");
    }

    public boolean hasVoteTargetAnnounceCommands(int targetVoteCount) {
        if (getRewards().isSet("dailytarget.announce." + targetVoteCount)) {
            return true;
        }
        return false;
    }

    public List<String> getVoteTargetAnnounceCommands(int voteAmount) {
        return getRewards().getStringList("dailytarget.announce." + voteAmount);
    }

    public String getVoteTargetAnnouncement() {
        String voteTargetAnnouncement = getRewards().getString("dailytarget.reached");
        return ChatColor.translateAlternateColorCodes('&', voteTargetAnnouncement);
    }

    public boolean isUsingDailyTarget() {
        return plugin.getConfig().getBoolean("ultimatevotes.votes.usedailytarget");
    }

    public List<String> getDailyTargetRewards() {
        return getRewards().getStringList("dailytarget.rewards");
    }

    public boolean isJoinMessagesEnabled() {
        return plugin.getConfig().getBoolean("ultimatevotes.votes.joinmessage.enabled");
    }

    public boolean isVoteBroadcastOnlineEnabled() {
        return plugin.getConfig().getBoolean("ultimatevotes.votes.broadcast.online");
    }

    public boolean isVoteBroadcastEnabled() {
        return plugin.getConfig().getBoolean("ultimatevotes.votes.broadcast.enabled");
    }

    /**
     * is the plugin maintaining a connection to the sql database
     * if true it will attempt to keep on connection open
     * if false it will create a new connection for each query and close it after completion
     * @return true if enabled false if not
     */
    public boolean isMaintainConnection() {
        return plugin.getConfig().getBoolean("ultimatevotes.mysql.maintainconnection");
    }

    public List<String> getVoteReminderDisabledWorlds() {
        return plugin.getConfig().getStringList("ultimatevotes.votes.votereminder.disabledworlds");
    }

    public boolean isVoteReminderDisabled(String worldNameIn) {
        for(String worldName: getVoteReminderDisabledWorlds()) {
            if(worldNameIn.equals(worldName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * is the claim gui enabled for claiming instead of using command based claiming
     * @return true if enabled, false if not
     */
    public boolean isClaimGUIEnabled() {
        return plugin.getConfig().getBoolean("ultimatevotes.rewards.claiming.useclaimgui");
    }

}
