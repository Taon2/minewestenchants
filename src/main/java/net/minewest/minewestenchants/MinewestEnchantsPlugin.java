package net.minewest.minewestenchants;

import net.minewest.minewestenchants.command.EnchantedBookCommand;
import net.minewest.minewestenchants.enchants.EnchantmentManager;
import net.minewest.minewestenchants.gui.GenerateGUI;
import net.minewest.minewestenchants.listener.ArmorEquipListener;
import net.minewest.minewestenchants.listener.WorldListener;
import net.minewest.minewestenchants.mechanics.Mechanics;
import org.bukkit.plugin.java.JavaPlugin;

public class MinewestEnchantsPlugin extends JavaPlugin {

    private static MinewestEnchantsPlugin instance;

    public static MinewestEnchantsPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        MinewestEnchantsPlugin.getInstance().getServer().getPluginManager()
                .registerEvents(new ArmorEquipListener(), this);
        MinewestEnchantsPlugin.getInstance().getServer().getPluginManager()
                .registerEvents(new WorldListener(), this);

        EnchantmentManager.getInstance();
        Mechanics.init();
        this.getCommand("enchantedbook").setExecutor(new EnchantedBookCommand());
    }

    @Override
    public void onDisable() {
    }
}
