package net.teozfrank.ultimatevotes.main;

import com.vexsoftware.votifier.model.VotifierEvent;
import net.teozfrank.ultimatevotes.api.MaterialHelper;
import net.teozfrank.ultimatevotes.api.TitleActionbar;
import net.teozfrank.ultimatevotes.api.UUIDFetcher;
import net.teozfrank.ultimatevotes.api.WorldEditSelectionHelper;
import net.teozfrank.ultimatevotes.discord.DiscordFileManager;
import net.teozfrank.ultimatevotes.discord.DiscordWebhookManager;
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
import java.text.SimpleDateFormat;
import java.util.*;

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
    private WorldEditSelectionHelper worldEditSelectionHelper;
    private MaterialHelper materialHelper;
    private DiscordFileManager discordFileManager;
    private DiscordWebhookManager discordWebhookManager;

    public UltimateVotes() {
    }




    @Override
    public void onEnable() {
        errorCount = 0;
        version = this.getDescription().getVersion();
        this.debug = this.getConfig().getBoolean("ultimatevotes.debug.enabled");

        if(this.getDescription().getVersion().contains("dev")) {
            SendConsoleMessage.warning("---------------------------------------------");
            SendConsoleMessage.warning("This is a development version of UltimateVotes, "
                    + "it is recommended to backup your entire UltimateVotes plugin folder and database before running this build.");
            SendConsoleMessage.warning("This version is also not intended to be used on a live production server, use at your own risk.");
            SendConsoleMessage.warning("---------------------------------------------");
        }

        this.setupDependencies();
        this.setupMaterialHelper();
        this.setupWorldEditSelectionHelper();
        this.setupUUIDFetcher();

        this.fileManager = new FileManager(this);//initialise our file manager as the methods below require it

        this.rewardsManager = new RewardsManager(this);
        this.messageManager = new MessageManager(this);
        this.discordFileManager = new DiscordFileManager(this);

        this.discordWebhookManager = new DiscordWebhookManager(this);

        if(this.isDebugEnabled()) { SendConsoleMessage.debug("Debug mode enabled!"); }
        this.setupConfigs();
        this.checkConfigVersions();
        pluginPrefix = ChatColor.translateAlternateColorCodes('&', getFileManager().getMessages().getString("messages.prefix"));
        lineBreak = getMessageManager().getLineBreak();

        this.registerEvents();
        if(errorCount != 0) {
            SendConsoleMessage.warning(errorCount + " of your config files are outdated, please check the above log to see were they updated correctly.");
        }

        this.databaseManager = new DatabaseManager(this);
        this.voteManager = new VoteManager(this);
        this.guiManager = new GUIManager(this);
        this.signManager = new SignManager(this);

        new SetSignLocation(this);
        new PlayerJoin(this);
        new CheckVotes(this);
        new RewardEventTest(this);


        getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                loadVotes();
                //TODO move this into database manager as it is making two connections to the database.
            }
        });
        this.remindPlayers();
        this.registerChannels();
        this.registerCommands();
        this.checkForUpdates();
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

    public void checkForUpdates() {
        if(fileManager.isUpdateCheckEnabled()) {
            try {
                String spigotVersion = Util.getSpigotVersion();
                if(! spigotVersion.equals(this.getDescription().getVersion())) {
                    SendConsoleMessage.info(ChatColor.GOLD + "---------------------------------------------------");
                    SendConsoleMessage.info(ChatColor.GOLD + "There is a new update available from spigot. Version: " + spigotVersion);
                    SendConsoleMessage.info(ChatColor.GOLD + "---------------------------------------------------");
                }
            } catch (NullPointerException e) {
                SendConsoleMessage.error("Unable to check for updates :(");
            }

        }
    }

    private boolean setupWorldEditSelectionHelper() {
        String version = this.getWorldEditVersion();

        if(version == null ) {
            SendConsoleMessage.warning("WorldEdit plugin not found, WorldEdit related features will not work!");
            return true;
        }

        if(isDebugEnabled()) {
            SendConsoleMessage.debug("WorldEdit Version: " + version);
        }
        String[] legacyVersions =  { "6." };
        String[] latestVersions = {"7."};

        boolean legacy = false;
        boolean latest = false;

        for(String legacyVersion: legacyVersions) {
            if(version.startsWith(legacyVersion)) {
                legacy = true;
                SendConsoleMessage.info("WorldEdit Selection Helper identified as legacy.");
            }
        }
        if(! legacy) {
            for(String latestVersion: latestVersions) {
                if(version.startsWith(latestVersion)) {
                    latest = true;
                    SendConsoleMessage.info("WorldEdit Selection Helper identified as latest.");
                }
            }
        }

        if(! legacy && ! latest) {
            SendConsoleMessage.warning("WorldEdit version not identified as legacy or latest, defaulting to latest. "
                    + "This can usually happen if you are using a WorldEdit version not matching 6.X or 7.X.");
            latest = true;
        }
        Class<?> clazz;
        try {
            if(legacy) {
                clazz = Class.forName("net.teozfrank.ultimatevotes.worldedit.legacy.WorldEditLegacy");
            } else {
                clazz = Class.forName("net.teozfrank.ultimatevotes.worldedit.latest.WorldEditLatest");
            }

            if (WorldEditSelectionHelper.class.isAssignableFrom(clazz)) { // Make sure it actually implements NMS
                this.worldEditSelectionHelper = (WorldEditSelectionHelper) clazz.getConstructor().newInstance(); // Set our handler
            } else {
                SendConsoleMessage.error("WorldEdit Selection Helper is not assignable, instance not created!");
            }
        } catch (Exception e) {
            SendConsoleMessage.error("WorldEdit Selection Helper setup failed: " + e.getMessage());
            return false;
        }
        SendConsoleMessage.info("WorldEdit Selection Helper setup complete.");
        return true;

    }

    private boolean setupMaterialHelper() {
        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        if(isDebugEnabled()) {
            SendConsoleMessage.debug("Material Helper Server NMS Version: " + version);
        }

        String[] legacyVersions =  { "v1_8_R1", "v1_8_R2", "v1_8_R3", "v1_9_R1", "v1_9_R2",
                "v1_10_R1", "v1_10_R1", "v1_11_R1", "v1_11_R1", "v1_12_R1" };
        String[] latestVersions = {"v1_13_R1",  "v1_13_R2", "v1_14_R1", "v1_15_R1"};

        boolean legacy = false;
        boolean latest = false;

        for(String legacyVersion: legacyVersions) {
            if(version.equals(legacyVersion)) {
                legacy = true;
                SendConsoleMessage.info("Material Helper identified as legacy.");
            }
        }
        if(! legacy) {
            for(String latestVersion: latestVersions) {
                if(version.equals(latestVersion)) {
                    latest = true;
                    SendConsoleMessage.info("Material Helper identified as latest.");
                }
            }
        }

        if(! legacy && ! latest) {
            SendConsoleMessage.warning("Material Helper NMS version not identified as legacy or latest, defaulting to latest. This can usually happen if you are using a newer version of spigot.");
            latest = true;
        }

        Class<?> clazz;

        try {
            if(legacy) {
                clazz = Class.forName("net.teozfrank.ultimatevotes.materialhelper.legacy.MaterialHelperLegacy");
            } else {
                clazz = Class.forName("net.teozfrank.ultimatevotes.materialhelper.latest.MaterialHelperLatest");
            }

            if (MaterialHelper.class.isAssignableFrom(clazz)) { // Make sure it actually implements NMS
                this.materialHelper = (MaterialHelper) clazz.getConstructor().newInstance(); // Set our handler
            } else {
                SendConsoleMessage.error("Material Helper is not assignable, instance not created!");
            }
        } catch (Exception e) {
            SendConsoleMessage.error("Material Helper setup failed: " + e.getMessage());
            return false;
        }
        SendConsoleMessage.info("Material Helper setup complete.");
        return true;
    }

    private boolean setupUUIDFetcher() {
        String packageName = this.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        if(isDebugEnabled()) {
            SendConsoleMessage.debug("Server NMS Version: " + version);
        }

        String[] legacyVersions =  { "v1_8_R1", "v1_8_R2", "v1_8_R3", "v1_9_R1", "v1_9_R2",
                "v1_10_R1", "v1_10_R1", "v1_11_R1", "v1_11_R1", "v1_12_R1", "v1_13_R1",  "v1_13_R2" };
        String[] latestVersions = {"v1_14_R1", "v1_15_R1"};

        boolean legacy = false;
        boolean latest = false;

        for(String legacyVersion: legacyVersions) {
            if(version.equals(legacyVersion)) {
                legacy = true;
                SendConsoleMessage.info("UUID Fetcher identified as legacy.");
            }
        }
        if(! legacy) {
            for(String latestVersion: latestVersions) {
                if(version.equals(latestVersion)) {
                    latest = true;
                    SendConsoleMessage.info("UUID Fetcher identified as latest.");
                }
            }
        }

        if(! legacy && ! latest) {
            SendConsoleMessage.warning("NMS version not identified as legacy or latest, defaulting to latest. This can usually happen if you are using a newer version of spigot.");
            latest = true;
        }

        Class<?> clazz;

        try {
            if(legacy) {
                clazz = Class.forName("net.teozfrank.ultimatevotes.uuidfetcher.legacy.UUIDFetcherLegacy");
            } else {
                clazz = Class.forName("net.teozfrank.ultimatevotes.uuidfetcher.latest.UUIDFetcherLatest");
            }

            if (UUIDFetcher.class.isAssignableFrom(clazz)) { // Make sure it actually implements NMS
                this.uuidFetcher = (UUIDFetcher) clazz.getConstructor().newInstance(); // Set our handler
            } else {
                SendConsoleMessage.error("UUID helper is not assignable, instance not created!");
            }
        } catch (Exception e) {
            SendConsoleMessage.error("UUID Fetcher failed: " + e.getMessage());
            return false;
        }
        SendConsoleMessage.info("UUID Fetcher setup complete.");
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
            } else {
                SendConsoleMessage.error("TitleActionBar is not assignable, instance not created!");
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
                SendConsoleMessage.error("Config update failed to migrate old settings! Please delete your config.yml" +
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
        /*if (!(new File(getDataFolder(), "discord.yml")).exists()) {
            getFileManager().saveDefaultDiscord();
        }*/
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
        if(! getFileManager().isMySqlEnabled()) {
            if(isDebugEnabled()) {
                SendConsoleMessage.debug("SQL is disabled, not closing connections");
            }
            return;
        }
        try {
            if(!getDatabaseManager().getConnection().isClosed()) {
                getDatabaseManager().getConnection().close();
            }
        } catch (SQLException e) {
            SendConsoleMessage.error("SQL Error! " + e.getMessage());
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
            try {
                getVoteManager().allVotes = getDatabaseManager().voteAllTime();
                getVoteManager().monthlyVotes = getDatabaseManager().voteMonthly();
                lastVotesUpdate = System.currentTimeMillis();
                SendConsoleMessage.info("Loading Votes Complete.");
            } catch (Exception ex) {
                SendConsoleMessage.error("Error loading votes into cache: " + ex.getMessage());
            }

            try {
                SendConsoleMessage.info("Now loading sign wall.");
                this.getServer().getScheduler().runTask(this, new Runnable() {

                    @Override
                    public void run() {
                        getSignManager().updateTopVotersOnWall();
                        SendConsoleMessage.info("Sign wall loading complete.");
                    }
                });
            } catch (Exception ex) {
                SendConsoleMessage.error("Error loading sign wall: " + ex.getMessage());
            }

            try {
                if (!(getVoteManager().monthlyVotes.size() <= 0 && getVoteManager().allVotes.size() <= 0)) {
                    SendConsoleMessage.info("Auto-Reload Interval set to " + ChatColor.AQUA + reloadInterval + ChatColor.GREEN + " Ticks, Task Starting!");
                    Bukkit.getScheduler().runTaskTimerAsynchronously(this, new AutoReloadVotesThread(this), reloadInterval, reloadInterval);
                } else {
                    SendConsoleMessage.info("Auto-Reloading " + ChatColor.RED + "DISABLED " + ChatColor.GREEN +
                            "as their are not any vote records to reload, when votes do exist, please " + ChatColor.AQUA + "reload the server.");
                }
                Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new TimedCmdThread(this), 20L * 60, 20L * 60);
            } catch (Exception ex) {
                SendConsoleMessage.error("Error trying to create setup reloading task." + ex.getMessage());
            }
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
            SendConsoleMessage.warning("WorldEdit dependency not found, setting wall signs will not work!");
        }
    }

    /**
     * Get the worldedit version
     * @return worldedit version, null if plugin is not loaded
     */
    public String getWorldEditVersion() {
        return this.getServer().getPluginManager().getPlugin("WorldEdit").getDescription().getVersion();
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

    public DiscordFileManager getDiscordFileManager() {
        return discordFileManager;
    }

    public DiscordWebhookManager getDiscordWebhookManager() {
        return discordWebhookManager;
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

    public WorldEditSelectionHelper getWorldEditSelectionHelper() {
        return worldEditSelectionHelper;
    }

    public MaterialHelper getMaterialHelper() {
        return materialHelper;
    }
}
