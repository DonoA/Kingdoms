package io.dallen.kingdoms.menus;

import io.dallen.kingdoms.customitems.CustomItemIndex;
import io.dallen.kingdoms.util.Bounds;
import lombok.Builder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@Builder
public class ChestSizingGUI {

    private final Location blockLoc;
    private final Consumer<Bounds> complete;
    private final Runnable exit;
    private final String title;

    public void sendToPlayer(Player player) {
        var proposedBounds = Bounds.builder()
                .blockX(blockLoc.getBlockX())
                .blockY(blockLoc.getBlockY())
                .blockZ(blockLoc.getBlockZ())
                .plusX(5)
                .minusX(5)
                .plusZ(5)
                .minusZ(5)
                .build();

        var defaultSizeText = proposedBounds.getSizeX() + "x" + proposedBounds.getSizeZ();
        var gui = new ChestGUI(title + " (" + defaultSizeText + ")", 5 * 9);
        gui.setClickHandler((menuEvent) -> {
            menuEvent.getPlayer().sendMessage("Clicked " + menuEvent.getSlot());
            switch (menuEvent.getSlot()) {
                case 4:
                    proposedBounds.add(1, 0, 0, 0);
                    break;
                case 13:
                    proposedBounds.add(-1, 0, 0, 0);
                    break;
                case 20:
                    proposedBounds.add(0, 0, 1, 0);
                    break;
                case 21:
                    proposedBounds.add(0, 0, -1, 0);
                    break;
                case 23:
                    proposedBounds.add(0, 0, 0, 1);
                    break;
                case 24:
                    proposedBounds.add(0, 0, 0, -1);
                    break;
                case 31:
                    proposedBounds.add(0, 1, 0, 0);
                    break;
                case 40:
                    proposedBounds.add(0, -1, 0, 0);
                    break;
                case 22:
                    complete.accept(proposedBounds);
                    break;
            }
            var sizeText = proposedBounds.getSizeX() + "x" + proposedBounds.getSizeZ();
            gui.setOption(22, CustomItemIndex.SUBMIT.toItemStack(), sizeText);
            gui.setName(title + " (" + sizeText + ")");
            menuEvent.setNext(gui);
        });

        gui.setCloseEvent((close) -> {
            exit.run();
        });

        gui.setOption(4, CustomItemIndex.INCREASE.toItemStack(), "Increase +X");
        gui.setOption(13, CustomItemIndex.DECREASE.toItemStack(), "Decrease +X");
        gui.setOption(20, CustomItemIndex.INCREASE.toItemStack(), "Increase +Z");
        gui.setOption(21, CustomItemIndex.DECREASE.toItemStack(), "Decrease +Z");
        gui.setOption(22, CustomItemIndex.SUBMIT.toItemStack(), proposedBounds.getSizeX() + "x" + proposedBounds.getSizeZ());
        gui.setOption(23, CustomItemIndex.INCREASE.toItemStack(), "Increase -Z");
        gui.setOption(24, CustomItemIndex.DECREASE.toItemStack(), "Decrease -Z");
        gui.setOption(31, CustomItemIndex.INCREASE.toItemStack(), "Increase -X");
        gui.setOption(40, CustomItemIndex.DECREASE.toItemStack(), "Decrease -X");

        gui.sendMenu(player);
    }

}
