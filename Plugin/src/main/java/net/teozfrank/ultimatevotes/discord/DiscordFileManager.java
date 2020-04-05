package net.teozfrank.ultimatevotes.discord;

import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.FileManager;
import org.bukkit.ChatColor;

public class DiscordFileManager {

    private UltimateVotes plugin;
    private FileManager fm;

    public DiscordFileManager(UltimateVotes plugin) {
        this.plugin = plugin;
        this.fm = plugin.getFileManager();
    }

    public boolean isVoteWebhookEventEnabled() {
        return fm.getDiscord().getBoolean("discord.webhookevents.vote.enabled");
    }

    public String getVoteWebhookEventWebhookURL() {
        return fm.getDiscord().getString("discord.webhookevents.vote.webhookurl");
    }

    public String getVoteWebhookEventUsername() {
        return fm.getDiscord().getString("discord.webhookevents.vote.username");
    }

    public String getVoteWebhookEventTitle() {
        return fm.getDiscord().getString("discord.webhookevents.vote.title");
    }

    public String getVoteWebhookEventContent() {
        return fm.getDiscord().getString("discord.webhookevents.vote.content");
    }
}
