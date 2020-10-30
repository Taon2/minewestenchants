package net.minewest.minewestenchants.enchants;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Enchantment extends Listener {
    String getName();

    String getHandle();

    int getMinLevel();

    int getMaxLevel();

    boolean canEnchantItem(ItemStack item);

    List<String> getDescription();

    int hashCode();
}
