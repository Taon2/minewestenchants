package net.minewest.minewestenchants.mechanics;

import net.minewest.minewestenchants.enchants.Enchantment;
import net.minewest.minewestenchants.enchants.EnchantmentManager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class OriginalMechanics implements EnchantMechanics {

    @Override
    public int getItemResult(ItemStack item, Enchantment enchantment, int bookLevel) {
        int curr = EnchantmentManager.getInstance().getEnchantmentLevel(item, enchantment);
        int val = curr == bookLevel ? curr + 1 : Math.max(curr, bookLevel);
        if ((val > enchantment.getMaxLevel()
                && bookLevel <= enchantment.getMaxLevel())
                || bookLevel < curr) return -1;
        return val;
    }

    @Override
    public int getItemResult(ItemStack item, org.bukkit.enchantments.Enchantment enchantment, int bookLevel) {
        int curr = item.getEnchantmentLevel(enchantment);
        int val = curr == bookLevel ? curr + 1 : Math.max(curr, bookLevel);
        if ((val > enchantment.getMaxLevel()
                && bookLevel <= enchantment.getMaxLevel())
                || bookLevel < curr) return -1;
        return val;
    }

    @Override
    public int getBookResult(ItemStack book, Enchantment enchantment, int bookLevel) {
        int curr = EnchantmentManager.getInstance().getEnchantmentLevel(book, enchantment);
        int val = curr == bookLevel ? curr + 1 : Math.max(curr, bookLevel);
        if ((val > enchantment.getMaxLevel()
                && bookLevel <= enchantment.getMaxLevel())
                || bookLevel < curr) return -1;
        return val;
    }

    @Override
    public int getBookResult(ItemStack book, org.bukkit.enchantments.Enchantment enchantment, int bookLevel) {
        int curr = ((EnchantmentStorageMeta) book.getItemMeta()).getStoredEnchantLevel(enchantment);
        int val = curr == bookLevel ? curr + 1 : Math.max(curr, bookLevel);
        if ((val > enchantment.getMaxLevel()
                && bookLevel <= enchantment.getMaxLevel())
                || bookLevel < curr) return -1;
        return val;
    }

    @Override
    public String getMechanicsName() {
        return "ORIGINAL";
    }
}
