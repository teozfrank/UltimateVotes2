package net.teozfrank.ultimatevotes.discord;

import net.teozfrank.ultimatevotes.util.SendConsoleMessage;

import java.io.IOException;

public class DiscordWebhookManager {

    DiscordWebhook webhook;

    public DiscordWebhookManager(String url) {
        this.webhook = new DiscordWebhook(url);
    }

    public boolean sendVoteNotification(String playername, String website) {
        webhook.setContent(playername + "voted for the server on " + website);
        webhook.setUsername("UltimateVotes");
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
