package net.poweredbyhate.dropeditor;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Created by Lax on 6/6/2017.
 */
public class MobInv implements InventoryHolder{

    private DropEditor plugin;

    public MobInv(DropEditor plugin) {
        this.plugin = plugin;
    }

    @Override
    public Inventory getInventory() {
        return Bukkit.createInventory(null, 54);
    }
}
