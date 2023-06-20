package io.dallen.kingdoms.craftbukkit;

import com.google.gson.Gson;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketPlayInWindowClick;
import net.minecraft.world.inventory.Containers;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftContainer;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class CraftPacketUtil {

    private static Gson gson = new Gson();

    public static String getPacketItemName(PacketPlayInWindowClick packet) {
        try {
            ItemStack craftStack = packet.e();
            NBTTagCompound nbt = craftStack.v();
            NBTTagCompound displayNBT = nbt.p("display");
            String nameJson = displayNBT.l("Name");

            Map<String, Object> meta = gson.fromJson(nameJson, Map.class);
            return (String) meta.get("text");
        } catch (NullPointerException ex) {
            return null;
        }
    }

//    public static void openCustomInventory(Inventory inv) {
//        Containers<?> container = CraftContainer.getNotchInventoryType(inventory)
//    }

}
