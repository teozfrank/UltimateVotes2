package net.teozfrank.ultimatevotes.main;

import com.vexsoftware.votifier.model.VotifierEvent;
import net.teozfrank.ultimatevotes.api.TitleActionbar;
import net.teozfrank.ultimatevotes.api.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import net.teozfrank.ultimatevotes.commands.*;
import net.teozfrank.ultimatevotes.events.*;
import net.teozfrank.ultimatevotes.threads.AutoReloadVotesThread;
import net.teozfrank.ultimatevotes.threads.CheckTrialThread;
import net.teozfrank.ultimatevotes.threads.RemindPlayersToVoteThread;
import net.teozfrank.ultimatevotes.threads.TimedCmdThread;
import net.teozfrank.ultimatevotes.util.*;

import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class UltimateVotes extends JavaPlugin {

    private static long lastVotesUpdate;
    private boolean debug;
    private static DatabaseManager databaseManager;
    private static VoteManager voteManager;
    private FileManager fileManager;
    private RewardsManager rewardsManager;
    private MessageManager messageManager;
    private static String version;
    private static String pluginPrefix;
    private static String lineBreak;
    private int errorCount;
    private SignManager signManager;
    private GUIManager guiManager;
    private boolean isTrail;
    public static final String INCOMING_CHANNEL_NAME = "uv:rewards";
    private TitleActionbar titleActionbar;

    private UUIDFetcher uuidFetcher;

    public UltimateVotes() {
    }


    @Override
    public void onEnable() {
        this.isTrail = false;
        errorCount = 0;
        version = this.getDescription().getVersion();
        if(isTrail) {
            if(!this.checkTrial()) {
                return;
            }
        }
        if(this.getDescription().getVersion().contains("dev")) {
            SendConsoleMessage.warning("---------------------------------------------");
            SendConsoleMessage.warning("This is a development version of UltimateVotes, "
                    + "it is recommended to backup your entire UltimateVotes plugin folder and database before running this build.");
            SendConsoleMessage.warning("This version is also not intended to be used on a live production server, use at your own risk.");
            SendConsoleMessage.warning("---------------------------------------------");
        }
        this.fileManager = new FileManager(this);//initialise our file manager as the methods below require it
        this.rewardsManager = new RewardsManager(this);
        this.messageManager = new MessageManager(this);
        this.signManager = new SignManager(this);
        this.debug = this.getConfig().getBoolean("ultimatevotes.debug.enabled");
        if(this.isDebugEnabled()) { SendConsoleMessage.debug("Debug mode enabled!"); }
        this.setupConfigs();
        this.checkConfigVersions();
        pluginPrefix = ChatColor.translateAlternateColorCodes('&', getFileManager().getMessages().getString("messages.prefix"));
        lineBreak = getMessageManager().getLineBreak();
        this.registerCommands();
        this.registerEvents();
        if(errorCount != 0) {
            SendConsoleMessage.warning(errorCount + " of your config files are outdated, please check the above log to see were they updated correctly.");
        }
        this.setupUUIDFetcher();
        this.databaseManager = new DatabaseManager(this);
        this.voteManager = new VoteManager(this);

        new SetSignLocation(this);
        new PlayerJoin(this);
        new CheckVotes(this);
        new RewardEventTest(this);
        this.guiManager = new GUIManager(this);

        getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                loadVotes();
            }
        });
        this.remindPlayers();
        this.setupDependencies();


        this.registerChannels();
        if(this.isTrail) {
            this.getServer().getScheduler().runTaskTimer(this, new CheckTrialThread(this), 20000L, 20000L);
        }
    }

    public boolean checkTrial() {
        Date todaysDate = new Date(System.currentTimeMillis());
        SimpleDateFormat myFormat = new SimpleDateFormat("dd MM yyyy");
        String inputString1 = "10 06 2015";
        Date expiryDate = null;
        try {
            expiryDate = myFormat.parse(inputString1);
        } catch (ParseException e) {
            this.onDisable();
            this.getServer().getPluginManager().disablePlugin(this);
            SendConsoleMessage.warning("Date parse failed, disabling plugin!");
            return false;
        }
        long diff = expiryDate.getTime() - todaysDate.getTime();
        long daysLeft = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        SendConsoleMessage.severe("This is a trial version of Ultimatevotes, it will expire in " + daysLeft + " day(s), after that you must purchase a full copy.");
        if(daysLeft <= 0) {
            SendConsoleMessage.severe("Trial has expired, Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            if(new File("plugins/UltimateVotes.jar").exists()) {
                File file = new File("plugins/UltimateVotes.jar");
                file.deleteOnExit();
            }
            return false;
        }
        return true;
    }


    public void registerChannels() {
        getServer().getMessenger().registerIncomingPluginChannel(this, INCOMING_CHANNEL_NAME, new ChannelListener(this));
    }


    private void checkConfigVersions() {
        FileManager fm = this.getFileManager();

        if(fm.getRewards().getDouble("configversion") != 1.4) {
            SendConsoleMessage.warning("Your " + ChatColor.AQUA + "rewards.yml " +
                    ChatColor.RED + " is out of date!");
            errorCount++;
        }

        if(getConfig().getDouble("ultimatevotes.configversion") != 2.7) {
            SendConsoleMessage.warning("Your " + ChatColor.AQUA + "config.yml " +
                    ChatColor.RED + " is out of date!");
            SendConsoleMessage.info("Updating config.yml file.");
            updateConfig();
            errorCount++;
        }

        if(fm.getMessages().getDouble("configversion") != 2.7) {
            SendConsoleMessage.warning("Your " + ChatColor.AQUA + "messages.yml "+
                    ChatColor.RED +" is out of date!");
            SendConsoleMessage.info("Updating messages.yml file.");
            updateMessagesConfig();
            errorCount++;
        }

    }

    private boolean setupUUIDFetcher() {
        String packageName = this.getServer().getClass().getPackage().getName();
        // Get full package string of CraftServer.
        // org.bukkit.craftbukkit.version
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        SendConsoleMessage.info("Server NMS Version: " + version);
        // Get the last element of the package

        //TODO check for older then load older class.

        try {
            final Class<?> clazz = Class.forName("net.teozfrank.ultimatevotes.uuidfetcher.latest.UUIDFetcherLatest");
            // Check if we have a NMSHandler class at that location.
            if (UUIDFetcher.class.isAssignableFrom(clazz)) { // Make sure it actually implements NMS
                this.uuidFetcher = (UUIDFetcher) clazz.getConstructor().newInstance(); // Set our handler
            }
            SendConsoleMessage.info("UUID Fetcher setup complete.");
        } catch (final Exception e) {
            if(isDebugEnabled()) {
                SendConsoleMessage.warning("Error setting up UUID Fetcher. " + e.getMessage());
            }
            return false;
        }
        return true;
    }

    private boolean setupTitleActionBar() {
        String packageName = this.getServer().getClass().getPackage().getName();
        // Get full package string of CraftServer.
        // org.bukkit.craftbukkit.version
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        SendConsoleMessage.info("Server NMS Version: " + version);
        // Get the last element of the package

        try {
            final Class<?> clazz = Class.forName("net.teozfrank.ultimatevotes.nms." + version + ".NMSHandler");
            // Check if we have a NMSHandler class at that location.
            if (TitleActionbar.class.isAssignableFrom(clazz)) { // Make sure it actually implements NMS
                this.titleActionbar = (TitleActionbar) clazz.getConstructor().newInstance(); // Set our handler
            }
        } catch (final Exception e) {
            if(isDebugEnabled()) {
                SendConsoleMessage.warning("Error setting up NMS Class. " + e.getMessage());
            }
            return false;
        }
        SendConsoleMessage.info("Loading support for " + version);
        return true;
    }

    private void updateMessagesConfig() {
        FileConfiguration messages = this.getFileManager().getMessages();
        if(messages.getDouble("configversion") == 1.9) {
            messages.set("configversion", 2.0);
            List<String> commandList = new ArrayList<String>();
            commandList.add("&a/vote - &6Brings up this message.");
            commandList.add("&a/vote claim - &6Claim your vote rewards.");
            commandList.add("&a/vote top - &6View the top voters this month.");
            commandList.add("&a/vote alltime - &6View the top voters of all time.");
            commandList.add("&a/vote mtop - &6View your vote count for this month.");
            commandList.add("&a/vote malltime - &6View your vote count for all time.");
            commandList.add("&a/vote about - &6More about this plugin.");
            commandList.add("&a/vote rewards - &6List the rewards for voting.");
            getFileManager().getMessages().set("messages.vote.commandlist", commandList);
            getFileManager().getMessages().set("messages.vote.title", "&6              UltimateVotes Command list            ");
            getFileManager().saveMessages();
            getFileManager().reloadMessages();
            SendConsoleMessage.info("Config update complete!");
        }

        if(messages.getDouble("configversion") == 2.0) {
            SendConsoleMessage.debug("Config version of messages is 2.0");
            messages.set("configversion", 2.1);
            messages.set("messages.rewardlist.title", "             &6Vote Rewards page &b%page%                     ");
            messages.set("messages.rewardlist.pages.1",
                    Arrays.asList("&bPlease list your rewards here by editing the messages.yml", "&6Every time you vote you get &b500 cash"));
            messages.set("messages.rewardlist.pages.2",
                    Arrays.asList("&6This is another example reward list page", "&61 Vote: &b 5 God Apples"));
            messages.set("messages.rewardlist.footer", "&6use &b/vote rewards %nextpage% &6to go to the next page of rewards.");
            getFileManager().saveMessages();
            getFileManager().reloadMessages();
            SendConsoleMessage.info("Messages config update complete!");
            return;
        }

        if(messages.getDouble("configversion") == 2.1) {
            SendConsoleMessage.debug("Config version of messages is 2.1");
            messages.set("configversion", 2.2);
            messages.set("messages.mtop.message", "&6   You have a total of &b%votecount% &6 votes this month");
            messages.set("messages.malltime.message", "&6  You have a total of &b%votecount% &6votes alltogether");
            getFileManager().saveMessages();
            getFileManager().reloadMessages();
            SendConsoleMessage.info("Messages config update complete!");
            return;
        }

        if(messages.getDouble("configversion") == 2.2) {
            SendConsoleMessage.debug("Config version of messages is 2.2");
            messages.set("configversion", 2.3);
            List<String> claimCmdMessages = new ArrayList<String>();
            claimCmdMessages.add("      &6Claim commands          ");
            claimCmdMessages.add("");
            claimCmdMessages.add("&a/claim all - &6Use all your claims on this server.");
            claimCmdMessages.add("&a/claim <amount> - &6Claim a certain amount on this server.");
            claimCmdMessages.add("");
            claimCmdMessages.add("&a You have a total of %unclaimedvotes% unclaimed votes.");
            messages.set("messages.claim.commandlist", claimCmdMessages);
            getFileManager().saveMessages();
            getFileManager().reloadMessages();
            SendConsoleMessage.info("Messages config update complete!");
            return;
        }

        if(messages.getDouble("configversion") == 2.3) {
            SendConsoleMessage.debug("Config version of messages is 2.3");
            messages.set("configversion", 2.4);
            messages.set("messages.claim.success", "&6You have successfully claimed &b%claimamount% &6unclaimed vote(s)!");
            getFileManager().saveMessages();
            getFileManager().reloadMessages();
            SendConsoleMessage.info("Messages config update complete!");
            return;
        }

        if(messages.getDouble("configversion") == 2.4) {
            SendConsoleMessage.debug("Config version of messages is 2.4");
            messages.set("configversion", 2.5);
            messages.set("messages.toplistformat", "&d%position%. &b%player% &dhas &b%votecount% &dvotes");
            messages.set("messages.claim.nounclaimedvotes", "&cYou do not have any unclaimed votes to claim!");
            messages.set("messages.claim.notenoughvotes", "&cYou do not have enough unclaimed votes to claim that much!");
            messages.set("messages.claim.invalidamount", "&cYou cannot claim that many votes!");
            messages.set("messages.claim.notnumber", "&cYou must input a number or /claim all for this command!");
            getFileManager().saveMessages();
            getFileManager().reloadMessages();
            SendConsoleMessage.info("Messages config update complete!");
            return;
        }

        if(messages.getDouble("configversion") == 2.5) {
            SendConsoleMessage.debug("Config version of messages is 2.5");
            messages.set("configversion", 2.6);
            messages.set("messages.claimgui.title", "Claim Rewards GUI");
            getFileManager().saveMessages();
            getFileManager().reloadMessages();
            SendConsoleMessage.info("Messages config update complete!");
            return;
        }

        if(messages.getDouble("configversion") == 2.6) {
            messages.set("configversion", 2.7);
            messages.set("messages.errors.subcmdnotvalid", "&c\"%subcmd%\" is not valid for the %cmd% command!");
            messages.set("messages.rewardlist.notnumber", "&cYou must enter a number for this command!");
            messages.set("messages.rewardlist.lastpage", "&6This is the last page of the reward list.");
            messages.set("messages.errors.unknowncmd", "&cUnknown command!");
            getFileManager().saveMessages();
            getFileManager().reloadMessages();
            SendConsoleMessage.info("Messages config update complete!");
        }

        if(!(messages.getDouble("configversion") == 2.7)) {
            SendConsoleMessage.warning("Error in updating messages config. No update found for the config version you are using! Have you changed it?");
        }

    }

    private void updateConfig() {
        if(getConfig().getDouble("ultimatevotes.configversion") == 1.9) {
            getConfig().set("ultimatevotes.configversion", 2.0);
            getConfig().set("ultimatevotes.votes.joinmessage.enabled", true);
            saveConfig();
            reloadConfig();
            SendConsoleMessage.info("Config update complete!");
        }

        if(getConfig().getDouble("ultimatevotes.configversion") == 2.0) {
            getConfig().set("ultimatevotes.configversion", 2.1);
            getConfig().set("ultimatevotes.votes.broadcast.online", true);
            getConfig().set("ultimatevotes.votes.broadcast.enabled", true);
            saveConfig();
            reloadConfig();
            SendConsoleMessage.info("Config update complete!");
        }

        if(getConfig().getDouble("ultimatevotes.configversion") == 2.1) {
            getConfig().set("ultimatevotes.configversion", 2.2);
            getConfig().set("ultimatevotes.mysql.maintainconnection", true);
            saveConfig();
            reloadConfig();
            SendConsoleMessage.info("Config update complete!");
        }

        if(getConfig().getDouble("ultimatevotes.configversion") == 2.2) {
            SendConsoleMessage.debug("Config version of config.yml is 2.2");
            getConfig().set("ultimatevotes.configversion", 2.3);
            List<String> disabledWorlds = new ArrayList<String>();
            disabledWorlds.add("exampledisabledworld");
            getConfig().set("ultimatevotes.votes.votereminder.disabledworlds", disabledWorlds);
            saveConfig();
            reloadConfig();
            SendConsoleMessage.info("Config update complete!");
            return;
        }

        if(getConfig().getDouble("ultimatevotes.configversion") == 2.3) {
            SendConsoleMessage.debug("Config version of config.yml is 2.3");
            getConfig().set("ultimatevotes.configversion", 2.4);
            getConfig().set("ultimatevotes.rewards.useclaimgui", false);
            saveConfig();
            reloadConfig();
            SendConsoleMessage.info("Config update complete!");
            return;
        }

        if(getConfig().getDouble("ultimatevotes.configversion") == 2.4) {
            SendConsoleMessage.debug("Config version of config.yml is 2.4, updating to 2.5");
            getConfig().set("ultimatevotes.configversion", 2.5);

            boolean useClaimCommand = getConfig().getBoolean("ultimatevotes.rewards.useclaimcommand");
            boolean useClaimGUI = getConfig().getBoolean("ultimatevotes.rewards.useclaimgui");
            SendConsoleMessage.debug("Current use claim command: " + useClaimCommand);
            SendConsoleMessage.debug("Current use claim gui: " + useClaimGUI);


            SendConsoleMessage.debug("removing settings from config.");
            getConfig().set("ultimatevotes.rewards.useclaimcommand", null);
            getConfig().set("ultimatevotes.rewards.useclaimgui", null);

            String newBasepath = "ultimatevotes.rewards.claiming.";
            SendConsoleMessage.debug("setting new config paths.");
            getConfig().set(newBasepath + "useclaimcommand", useClaimCommand);
            getConfig().set(newBasepath + "useclaimgui", useClaimGUI);
            getConfig().set(newBasepath + "rewardonline", useClaimGUI);

            saveConfig();
            reloadConfig();

            boolean useClaimCommandNewPath = fileManager.useClaimCommand();
            boolean useClaimGUINewPath = fileManager.isClaimGUIEnabled();

            if(useClaimCommandNewPath == useClaimCommand && useClaimGUINewPath == useClaimGUI) {
                SendConsoleMessage.info("Config update completed successfully!");
            } else {
                SendConsoleMessage.severe("Config update failed to migrate old settings! Please delete your config.yml" +
                        " and let a new one generate and copy the settings over! If you fail to do this problems may occur!");
            }


            return;
        }

        if(getConfig().getDouble("ultimatevotes.configversion") == 2.5) {
            SendConsoleMessage.debug("Config version of config.yml is 2.5, updating to 2.6");
            getConfig().set("ultimatevotes.configversion", 2.6);

            getConfig().set("ultimatevotes.topvoterslog.enabled", true);
            getConfig().set("ultimatevotes.topvoterslog.limit", 5);
            getConfig().set("ultimatevotes.rewards.cumulativerewards.rewardbymonthlyvotescount", true);

            saveConfig();
            reloadConfig();

            SendConsoleMessage.info("Config update completed successfully!");
            return;
        }

        if(getConfig().getDouble("ultimatevotes.configversion") == 2.6) {
            SendConsoleMessage.debug("Config version of config.yml is 2.6, updating to 2.7");
            getConfig().set("ultimatevotes.configversion", 2.7);
            getConfig().set("ultimatevotes.votes.autoresetmonthlyvotes", true);

            saveConfig();
            reloadConfig();

            SendConsoleMessage.info("Config update completed successfully!");
            return;
        }



        if(!(getConfig().getDouble("ultimatevotes.configversion") == 2.6)) {
            SendConsoleMessage.warning("Error in updating config. No update found for the config version you are using! Have you changed it?");
        }

    }

    private void setupConfigs() {
        if (!(new File(getDataFolder(), "topvoters.yml")).exists()) {
            getFileManager().saveDefaultTopVoters();
        }
        if (!(new File(getDataFolder(), "rewards.yml")).exists()) {
            getFileManager().saveDefaultRewards();
        }
        if (!(new File(getDataFolder(), "config.yml")).exists()) {
            saveDefaultConfig();
        }
        if (!(new File(getDataFolder(), "messages.yml")).exists()) {
            getFileManager().saveDefaultMessages();
        }
    }

    @Override
    public void onDisable() {
        SendConsoleMessage.info("Stopping Tasks.");
        Bukkit.getScheduler().cancelTasks(this);
        SendConsoleMessage.info("Complete!");
        SendConsoleMessage.info("Clearing sign wall.");
        try {
            getSignManager().clearSignWall();
        } catch (NullPointerException e) {
            SendConsoleMessage.info("Wall of signs not set, not clearing.");
        }
        SendConsoleMessage.info("Closing SQL connection.");
        this.closeConnections();
        if(this.getServer().getPluginManager().getPlugin("Votifier") != null) {
            SendConsoleMessage.info("Unregistering vote event.");
            VotifierEvent.getHandlerList().unregister(this);
        }
        runAllPendingRewardCommands();
    }

    private void runAllPendingRewardCommands() {
        try {
            List<TimedCmd> timedCmdList =getRewardsManager().getTimedCommands();

            if(timedCmdList.isEmpty()) {
                return;
            }

            for(TimedCmd timedCmd: timedCmdList) {
                for(int start = 1; start < timedCmd.getCmds().length; start++) {
                   getServer().dispatchCommand(getServer().getConsoleSender(), timedCmd.getCmd(start));
                }
            }
        } catch (NullPointerException e) {

        }
    }

    private void closeConnections() {
        try {
            if(!getDatabaseManager().getConnection().isClosed()) {
                getDatabaseManager().getConnection().close();
            }
        } catch (SQLException e) {
            SendConsoleMessage.severe("SQL Error! " + e.getMessage());
        } catch (NullPointerException e) {
            //ignored
        }
    }

    public void registerCommands() {
        getCommand("vote").setExecutor(new VoteExecutor(this));
        getCommand("uv").setExecutor(new UltimateVotesListener(this));
        getCommand("votesites").setExecutor(new VExecutor());
        getCommand("voteclaim").setExecutor(new ClaimExecutor(this));
        getCommand("voterewards").setExecutor(new VoteRewardsExecutor(this));
    }

    public void registerEvents() {
        if(this.getServer().getPluginManager().getPlugin("Votifier") != null){
            Bukkit.getPluginManager().registerEvents(new PlayerVote(this), this);
            SendConsoleMessage.info("Votifier / NuVotifier found!, listening for votes!");
        }
        else {
            SendConsoleMessage.info("Votifier / NuVotifier NOT found, listening for votes has been "+ChatColor.RED+"DISABLED.");
        }
    }

    public void loadVotes() {

        long reloadInterval = this.getConfig().getLong("ultimatevotes.votes.autoreloadvotesinterval");

        if (this.getConfig().getBoolean("ultimatevotes.votes.loadonstartup")) {

            SendConsoleMessage.info("Loading Votes ENABLED.");
            SendConsoleMessage.info("Now Loading Votes.");
            lastVotesUpdate = System.currentTimeMillis();
            getVoteManager().allVotes = getDatabaseManager().voteAllTime();
            getVoteManager().monthlyVotes = getDatabaseManager().voteMonthly();
            SendConsoleMessage.info("Loading Votes Complete.");
            SendConsoleMessage.info("Now loading sign wall.");
            this.getServer().getScheduler().runTask(this, new Runnable() {

                @Override
                public void run() {
                    getSignManager().updateTopVotersOnWall();
                    SendConsoleMessage.info("Sign wall loading complete.");
                }
            });


            if (!(getVoteManager().monthlyVotes.size() <= 0 && getVoteManager().allVotes.size() <= 0)) {
                SendConsoleMessage.info("Auto-Reload Interval set to " + ChatColor.AQUA + reloadInterval + ChatColor.GREEN + " Ticks, Task Starting!");
                Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AutoReloadVotesThread(this), reloadInterval, reloadInterval);
            } else {
                SendConsoleMessage.info("Auto-Reloading " + ChatColor.RED + "DISABLED " + ChatColor.GREEN +
                        "as their are not any vote records to reload, when votes do exist, please " + ChatColor.AQUA + "reload the server.");
            }
            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TimedCmdThread(this), 20L * 60, 20L * 60);

        } else {
            SendConsoleMessage.info("Loading Votes " + ChatColor.RED + "Disabled" + ChatColor.GREEN + ", Enable in config once the" +
                    "tables have been created in the database.");
        }
    }

    public void remindPlayers() {
        if (this.getConfig().getBoolean("ultimatevotes.votes.votereminder.enabled")) {
            long remindInterval = this.getConfig().getLong("ultimatevotes.votes.votereminder.interval");
            SendConsoleMessage.info("Vote reminders ENABLED.");

            Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new RemindPlayersToVoteThread(this), remindInterval, remindInterval);
        }
    }

    /**
     * sets up the plugin main dependencies such as WorldEdit
     * disables the plugin if the required dependency is not present
     */
    private void setupDependencies() {
        if (this.getServer().getPluginManager().getPlugin("WorldEdit") != null) {
            SendConsoleMessage.info("WorldEdit found! hooking into plugin!");
        } else {
            SendConsoleMessage.warning("WorldEdit dependency not found, plugin disabled!");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }


    public static long getLastVotesUpdate() {
        return lastVotesUpdate;
    }

    public void setLastVotesUpdate(long lastAllVotesUpdate) {
        lastVotesUpdate = lastAllVotesUpdate;
    }

    public static String getPluginVersion(){
        return version;
    }

    public FileManager getFileManager() {
        return this.fileManager;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static VoteManager getVoteManager() {
        return voteManager;
    }

    public RewardsManager getRewardsManager() {
        return this.rewardsManager;
    }

    public MessageManager getMessageManager() {
        return this.messageManager;
    }

    public boolean isDebugEnabled() {
        return this.debug;
    }

    public SignManager getSignManager() {
        return signManager;
    }

    public static String getPluginPrefix() {
        return pluginPrefix;
    }

    public static String getLineBreak() {
        return lineBreak;
    }

    public String getLastUpdatedMinsFormatted() {
        long difference = System.currentTimeMillis() - getLastVotesUpdate();
        SimpleDateFormat mins = new SimpleDateFormat("mm");
        return mins.format(difference).toString();
    }

    public String getLastUpdatedSecondsFormatted() {
        long difference = System.currentTimeMillis() - getLastVotesUpdate();
        SimpleDateFormat seconds = new SimpleDateFormat("ss");
        return seconds.format(difference).toString();
    }

    public boolean isTrail() {
        return isTrail;
    }

    public GUIManager getGUIManager() {
        return guiManager;
    }

    public UUIDFetcher getUUIDFetcher() {
        return uuidFetcher;
    }
}
