package net.teozfrank.ultimatevotes.discord;

import net.teozfrank.ultimatevotes.main.UltimateVotes;
import net.teozfrank.ultimatevotes.util.SendConsoleMessage;

import java.awt.*;
import java.io.IOException;

public class DiscordWebhookManager {

    DiscordWebhook webhook;
    private UltimateVotes plugin;

    public DiscordWebhookManager(UltimateVotes plugin) {
        this.plugin = plugin;
    }

    public boolean sendVoteNotification(String playername, String service, String ipAddress) {

        DiscordFileManager dfm = plugin.getDiscordFileManager();

        String webhookURL = dfm.getVoteWebhookEventWebhookURL();
        String webhookUsername = dfm.getVoteWebhookEventUsername();
        String webhookTitle = dfm.getVoteWebhookEventTitle();
        String webhookContent = dfm.getVoteWebhookEventContent();
        webhookContent = webhookContent.replaceAll("%playername%", playername);
        webhookContent = webhookContent.replaceAll("%service%", service);
        webhookContent = webhookContent.replaceAll("%ipaddress%", ipAddress);

        this.webhook = new DiscordWebhook(webhookURL);

        webhook.setUsername(webhookUsername);

        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle(webhookTitle)
                .setDescription(webhookContent)
                .setColor(Color.GREEN));

        webhook.setTts(true);
        try {
            webhook.execute();
        } catch (IOException e) {
            SendConsoleMessage.error("Failed to send vote notification: " + e.getMessage());
            return false;
        }
        return true;
    }
}
