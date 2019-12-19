package net.teozfrank.ultimatevotes.nms.v1_8_R3;

import net.teozfrank.ultimatevotes.api.TitleActionbar;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Created by Frank on 20/10/2019.
 */
public class NMSHandler implements TitleActionbar {

    @Override
    public void sendActionbar(Player player, String message) {
        IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + message + "\"}");
        PacketPlayOutChat bar = new PacketPlayOutChat(icbc, (byte)2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(bar);
    }

    @Override
    public void sendActionbar(Player player, Player sender, String message) {
        sendActionbar(player, message);
        sendActionbar(sender, message);
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        CraftPlayer craftplayer = (CraftPlayer) player;
        PlayerConnection connection = craftplayer.getHandle().playerConnection;
        IChatBaseComponent titleJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
        IChatBaseComponent subtitleJSON = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleJSON, fadeIn, stay, fadeOut);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleJSON);
        connection.sendPacket(titlePacket);
        connection.sendPacket(subtitlePacket);
    }

    @Override
    public void sendTitle(Player sender, Player acceptor, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        sendTitle(sender, title, subtitle, fadeIn, stay, fadeOut);
        sendTitle(acceptor, title, subtitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void sendBossbar(String title, String colorName, String styleName, Player player, Plugin plugin, int period) {
        player.sendMessage(title);
    }
}
