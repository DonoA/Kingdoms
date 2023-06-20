package io.dallen.kingdoms.packets;

import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.Plugin;

public class PacketListeners {

    public static void registerListeners(Plugin plugin, ProtocolManager protocolManager) {
        protocolManager.addPacketListener(new CustomGUI(plugin));
    }

}
