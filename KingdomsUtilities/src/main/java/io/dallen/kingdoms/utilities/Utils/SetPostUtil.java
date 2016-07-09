package io.dallen.kingdoms.utilities.Utils;

import io.dallen.kingdoms.utilities.KingdomsUtilities;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import java.util.ArrayList;

/**
 * Created by Icy on 6/30/16.
 */
public class SetPostUtil {

    public void setPost(Player playah, String[] args) {
        //get the block a player is looking at
        BlockIterator iterator = new BlockIterator(playah, 5);
        Block selectBlock = iterator.next();
        while (selectBlock.getType() == Material.AIR && iterator.hasNext()) {
            selectBlock = iterator.next();
        }

        //If the player is looking at a chest, see if they have a name in the args
        if (selectBlock.getType() == Material.CHEST) {
            if (args.length < 1) {
                playah.sendMessage("§cGive a name for the post office!");
            } else {
                //take the name in the args and make it one string
                int wordLength = args.length;
                int wordIter = 1;
                String word = args[0];
                while (wordLength > wordIter && args.length > 1) {
                    word = word.concat(" " + args[wordIter]);
                    wordIter++;
                }
                if (word.contains(".")) {
                    playah.sendMessage("§cYou can't put \".\" in the name!");
                } else {
                    //put the xyz of the chest in the config
                    //DO NOT SAVE DATA IN THE CONFIG, IT IS FOR CONFIG NOT SAVE DATA!
                    int xCoord = selectBlock.getX();
                    int yCoord = selectBlock.getY();
                    int zCoord = selectBlock.getZ();
                    KingdomsUtilities.getPlugin().getConfig().set("postOffice." + word + ".x", xCoord);
                    KingdomsUtilities.getPlugin().getConfig().set("postOffice." + word + ".y", yCoord);
                    KingdomsUtilities.getPlugin().getConfig().set("postOffice." + word + ".z", zCoord);
                    KingdomsUtilities.getPlugin().saveConfig();
                    playah.sendMessage("§aThe post office, \"" + word + "\", has been set!");
                }
            }
        } else {
            playah.sendMessage("§cThat's not a chest!");
        }
    }
}
