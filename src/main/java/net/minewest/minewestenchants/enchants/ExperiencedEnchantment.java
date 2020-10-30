package net.minewest.minewestenchants.enchants;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ExperiencedEnchantment extends BaseEnchantment {
    ExperiencedEnchantment() {
        super("EXPERIENCED", "Experienced", 1, 3);
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return item.getType().name().contains("SWORD");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null || event.getEntity() == null) return;

        ItemStack item = event.getEntity().getKiller().getInventory().getItemInHand();
        if (item == null || EnchantmentManager.getInstance().getEnchantmentLevel(item, EnchantmentManager.EXPERIENCED) <= 0) return;

        int level = EnchantmentManager.getInstance().getEnchantmentLevel(item, EnchantmentManager.EXPERIENCED);
        double multiplier = .25 * level;
        int initialXP = event.getDroppedExp();

        event.setDroppedExp((int)(initialXP + (initialXP * multiplier)));
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.WHITE + "Gives a bonus to experience",
                ChatColor.WHITE + "gained from killing mobs by",
                ChatColor.WHITE + "25% per enchantment level.",
                ChatColor.WHITE + "",
                ChatColor.DARK_PURPLE + "Attaches to Swords.",
                ChatColor.DARK_PURPLE + "Max Level: " + this.getMaxLevel()
        );
    }
}
