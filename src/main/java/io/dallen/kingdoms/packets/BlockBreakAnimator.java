package io.dallen.kingdoms.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import io.dallen.kingdoms.Kingdoms;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BlockBreakAnimator implements Listener {

    public final static int renderDistance = 30;

    @Data
    @AllArgsConstructor
    private static class BreakStage {
        float damageDone;
        int stage;
        int entityId;
    }

    private static Map<Location, BreakStage> breakStageMap = new HashMap<>();

    private static Random random = new Random();

    public static void setupRefresher(Plugin plugin) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, BlockBreakAnimator::refresh, 20 * 10, 20 * 10);
    }

    private static void refresh() {
        for (var breakStateEntry : breakStageMap.entrySet()) {
            sendBreakUpdate(breakStateEntry.getKey(), breakStateEntry.getValue());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var blockLoc = event.getBlock().getLocation();
        breakStageMap.remove(blockLoc);
    }

    public static boolean doDamage(Location l, float damage, boolean dropItem) {
        var blockType = l.getBlock().getType();
        if (!blockType.isSolid()) {
            return false;
        }

        var breakStage = breakStageMap.get(l);
        if (breakStage == null) {
            breakStage = new BreakStage(damage, 1, random.nextInt(Integer.MAX_VALUE));
        }

        if (breakStage.damageDone > blockType.getHardness()) {
            breakStage.stage += 1;
            breakStage.damageDone = breakStage.damageDone - blockType.getHardness();
        }

        if (breakStage.stage > 9) {
            if (dropItem) {
                l.getBlock().breakNaturally();
            } else {
                l.getBlock().setType(Material.AIR);
            }
            sendBreakUpdate(l, breakStage);
            breakStageMap.remove(l);
            return true;
        } else {
            sendBreakUpdate(l, breakStage);
            breakStageMap.put(l, breakStage);
            return false;
        }
    }

    private static void sendBreakUpdate(Location l, BreakStage breakStage) {
        for (var player : Bukkit.getOnlinePlayers()) {
            if (player.getLocation().distance(l) > renderDistance) {
                continue;
            }

            var breakPacket = new PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
            breakPacket.getIntegers()
                    .write(0, breakStage.entityId)
                    .write(1, breakStage.stage);
            var position = new BlockPosition(l.toVector());
            breakPacket.getBlockPositionModifier()
                    .write(0, position);

            Kingdoms.protocolManager.sendServerPacket(player, breakPacket);
        }

    }
}
