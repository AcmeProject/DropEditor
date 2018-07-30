package net.poweredbyhate.dropeditor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

import java.util.Arrays;

public class CommandDropEdit implements CommandExecutor {

    private DropEditor dropEditor;

    public CommandDropEdit(DropEditor dropEditor) {
        this.dropEditor = dropEditor;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && sender.hasPermission("dropedit.admin") && args.length >= 1) {
            if (args[0].equalsIgnoreCase("open") && args[1] != null) {
                if (Arrays.asList(EntityType.values()).contains(args[1].toUpperCase())) {
                    dropEditor.openDropInventory((Player) sender, args[1].toUpperCase());
                }
            }

            if (args[0].equalsIgnoreCase("debug")) {
                Inventory inv = Bukkit.createInventory(null, 54);
                for (EntityType e : EntityType.values()) {
                    ItemStack itemStack = new ItemStack(Material.MONSTER_EGG,  1);
                    SpawnEggMeta meta = (SpawnEggMeta) itemStack.getItemMeta();
                    try {
                        meta.setSpawnedType(e);
                        itemStack.setItemMeta(meta);
                        inv.addItem(itemStack);
                    } catch (IllegalArgumentException eh) {
                        eh.printStackTrace();
                    }
                }
                ((Player) sender).openInventory(inv);
            }
        }
        return false;
    }
}
