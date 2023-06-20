package io.dallen.kingdoms.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import io.dallen.kingdoms.events.AnvilRenameEvent;
import net.minecraft.network.protocol.game.PacketPlayInItemName;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CustomGUI extends PacketAdapter {

    public CustomGUI(Plugin plugin) {
        super(plugin, PacketType.Play.Client.ITEM_NAME);
    }

    public void onPacketReceiving(PacketEvent event) {
        var handle = event.getPacket().getHandle();
        if (!(handle instanceof PacketPlayInItemName)) {
            return;
        }
        event.getPlayer();
        var packet = (PacketPlayInItemName) handle;
        var renameEvent = new AnvilRenameEvent(event.getPlayer(), packet.a());
        Bukkit.getScheduler().callSyncMethod(getPlugin(), () -> {
            Bukkit.getPluginManager().callEvent(renameEvent);
            return null;
        });
    }
}
