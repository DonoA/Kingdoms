package io.dallen.kingdoms.kingdom;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class Kingdom {

    private static Map<String, Kingdom> allKingdoms = new HashMap<>();

    @Getter
    private final String name;

    @Getter
    private final Location claim;

    @Getter
    private final Player owner;

    private final List<Player> members = new ArrayList<>();

    public static void register(Kingdom kingdom) {
        allKingdoms.put(kingdom.getName(), kingdom);
    }
}
