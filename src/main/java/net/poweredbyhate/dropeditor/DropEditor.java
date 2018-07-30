package net.poweredbyhate.dropeditor;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;

public final class DropEditor extends JavaPlugin implements Listener {

    public HashMap<String, Inventory> drops = new HashMap<>();
    Random r = new Random();
    Boolean deubg = false;
    Boolean vanillaDrops = true;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        vanillaDrops = getConfig().getBoolean("vanillaDrops");
        loadFiles();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("dropedit").setExecutor(new CommandDropEdit(this));
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent ev) {
        debug(ev.getPlayer().getOpenInventory().getTitle());
        if (!ev.getPlayer().hasPermission("dropedit.admin")) {
            return;
        }
        if (ev.getRightClicked() instanceof Player) {
            return;
        }
        if (!ev.getPlayer().isSneaking()) {
            return;
        }
        ev.setCancelled(true);
        String type = ev.getRightClicked().getType().toString();
        openDropInventory(ev.getPlayer(), type);
    }

    public void openDropInventory(Player p, String entityType) {
        if (!drops.containsKey(entityType)) {
            debug("Putting");
            Inventory newinv = Bukkit.createInventory(new MobInv(this), 54, entityType);
            drops.put(entityType, newinv);
            p.openInventory(newinv);
        } else {
            p.openInventory(drops.get(entityType));
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent ev) {
        if (ev.getInventory().getHolder() instanceof MobInv) {
            debug("Closed");
            for (String s : drops.keySet()) {
                mobConfig(1,s,"data", Serialization.toBase64(drops.get(s)));
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent ev) {
        if (drops.containsKey(ev.getEntityType().toString())) {
            Inventory inv = drops.get(ev.getEntityType().toString());
            ItemStack is = inv.getItem(r.nextInt(inv.getSize()));
            if (is == null) {
                return;
            }
            ev.getEntity().getWorld().dropItemNaturally(ev.getEntity().getLocation(), is);
        }
        if (!vanillaDrops) {
            ev.getDrops().clear();
        }
    }

    public void loadFiles() {
        File folder = new File(getDataFolder(), "mobdata");
        if (!folder.exists()) {
            return;
        }
        try {
        Files.walk(Paths.get(folder.getAbsolutePath())).filter(file -> file.getFileName().toString().endsWith(".yml")).collect(Collectors.toList()).forEach(file -> loadData(file.getFileName().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadData(String type) {
        type = type.split("\\.")[0];
        getLogger().log(Level.INFO,"Loading " + type);
        try {
            Inventory newinv = Bukkit.createInventory(new MobInv(this), 54, type);
            newinv.setContents(Serialization.fromBase64(mobConfig(0, type,"","")).getContents());
            drops.put(type, newinv);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //0 get, 1 set
    public String mobConfig(int i, String type, Object data, Object payload) {
        File folder = new File(getDataFolder(), "mobdata");
        File mobconfig = new File(folder, type+".yml");
        FileConfiguration mconf = new YamlConfiguration();
        try {
            if (!folder.exists()) {
                folder.mkdirs();
            }
            if (!mobconfig.exists()) {
                mobconfig.createNewFile();
            }
            mconf.load(mobconfig);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        if (i == 0) {
            return mconf.getString("data");
        }

        if (i == 1) {
            mconf.set(data.toString(), payload);
            try {
                mconf.save(mobconfig);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public void debug(String s) {
        if (deubg) {
            System.out.println(s);
        }
    }
}
