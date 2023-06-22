package io.dallen.kingdoms.packets;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import io.dallen.kingdoms.Kingdoms;
import io.dallen.kingdoms.customblocks.CustomBlockListener;
import io.dallen.kingdoms.util.LocationUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
        Bukkit.getServer().getPluginManager().registerEvents(new BlockBreakAnimator(), plugin);
    }

    private static void refresh() {
        for (var breakStateEntry : breakStageMap.entrySet()) {
            sendBreakUpdate(breakStateEntry.getKey(), breakStateEntry.getValue());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        var blockLoc = event.getBlock().getLocation();
        removeBreakdata(blockLoc);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        var blockLoc = event.getBlock().getLocation();
        removeBreakdata(blockLoc);
    }

    private void removeBreakdata(Location blockLoc) {
        var breakStage = breakStageMap.remove(blockLoc);
        if (breakStage != null) {
            breakStage.stage = 10;
            sendBreakUpdate(blockLoc, breakStage);
        }
    }

    public static boolean doDamage(Location loc, float damage, boolean dropItem) {
        var blockLoc = LocationUtil.blockLoc(loc);
        var blockType = blockLoc.getBlock().getType();
        if (!blockType.isSolid()) {
            return true;
        }

        var breakStage = breakStageMap.get(blockLoc);
        if (breakStage == null) {
            breakStage = new BreakStage(0f, 1, random.nextInt(Integer.MAX_VALUE));
        }
        breakStage.damageDone += damage;

        if (breakStage.damageDone > blockType.getHardness()) {
            breakStage.stage += 1;
            breakStage.damageDone = breakStage.damageDone - blockType.getHardness();
        }

        if (breakStage.stage > 9) {
            if (dropItem) {
                blockLoc.getBlock().breakNaturally();
            } else {
                blockLoc.getBlock().setType(Material.AIR);
            }
            sendBreakUpdate(blockLoc, breakStage);
            breakStageMap.remove(blockLoc);
            return true;
        } else {
            sendBreakUpdate(blockLoc, breakStage);
            breakStageMap.put(blockLoc, breakStage);
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
