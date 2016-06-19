package io.dallen.Kingdoms.Kingdom.Structures.Types;

import io.dallen.Kingdoms.Kingdom.Plot;
import io.dallen.Kingdoms.Kingdom.Structures.Storage;
import io.dallen.Kingdoms.Kingdom.Vaults.BuildingVault;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.player.PlayerInteractEvent;


/**
 * Created by Icy on 6/18/16.
 */
public class PostOffice extends Plot implements Storage {

    public PostOffice(Plot p) {
        super(p);
    }
    
    @Override
    public BuildingVault getStorage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean interact(PlayerInteractEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasSpace() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean supplyNPC(NPC npc) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
