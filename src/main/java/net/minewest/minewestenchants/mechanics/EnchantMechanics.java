package net.minewest.minewestenchants.mechanics;

import net.minewest.minewestenchants.enchants.Enchantment;
import org.bukkit.inventory.ItemStack;

public interface EnchantMechanics {

    int getItemResult(ItemStack item, Enchantment enchantment, int bookLevel);

    int getItemResult(ItemStack item, org.bukkit.enchantments.Enchantment enchantment, int bookLevel);

    int getBookResult(ItemStack book, Enchantment enchantment, int bookLevel);

    int getBookResult(ItemStack book, org.bukkit.enchantments.Enchantment enchantment, int bookLevel);

    String getMechanicsName();
    
}
