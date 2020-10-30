package net.minewest.minewestenchants.enchants;

import net.minewest.minewestenchants.MinewestEnchantsPlugin;
import net.minewest.minewestenchants.events.ArmorEquipEvent;
import net.minewest.minewestenchants.listener.ArmorType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;

public class SonicEnchantment extends BaseEnchantment {
    SonicEnchantment() {
        super("SONIC", "Sonic", 1, 2);
    }

    @Override
    protected void postLoad() {
        super.postLoad();
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return item.getType().name().contains("BOOTS");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                if (player == null || !player.isOnline()) return; // in case they manage to log out in the first 2 ticks

                ItemStack item = player.getInventory().getBoots();

                if (item != null && EnchantmentManager.getInstance().getEnchantmentLevel(item, EnchantmentManager.SONIC) > 0) {
                    for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                        if (activePotionEffect.getType().equals(PotionEffectType.SPEED) && activePotionEffect.getDuration() > (Integer.MAX_VALUE / 2))
                            player.removePotionEffect(PotionEffectType.SPEED);
                    }

                    giveEffect(player, item);
                } else {
                    for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                        if (activePotionEffect.getType().equals(PotionEffectType.SPEED) && activePotionEffect.getDuration() > (Integer.MAX_VALUE / 2))
                            player.removePotionEffect(PotionEffectType.SPEED);
                    }
                }
            }
        }.runTaskLater(MinewestEnchantsPlugin.getInstance(), 2L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();

        if (event.getType() != ArmorType.BOOTS) return;

        //Checks for old boots with speed being removed
        if (event.getOldArmorPiece() != null && EnchantmentManager.getInstance().getEnchantmentLevel(event.getOldArmorPiece(), EnchantmentManager.SONIC) > 0) {
            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                if (activePotionEffect.getType().equals(PotionEffectType.SPEED) && activePotionEffect.getDuration() > (Integer.MAX_VALUE / 2))
                    player.removePotionEffect(PotionEffectType.SPEED);
            }
        } else {
            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                if (activePotionEffect.getType().equals(PotionEffectType.SPEED) && activePotionEffect.getDuration() > (Integer.MAX_VALUE / 2))
                    player.removePotionEffect(PotionEffectType.SPEED);
            }
        }
        //Checks for new boots with speed
        if (event.getNewArmorPiece() != null && EnchantmentManager.getInstance().getEnchantmentLevel(event.getNewArmorPiece(), EnchantmentManager.SONIC) > 0) {
            ItemStack item = event.getNewArmorPiece();

            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                if (activePotionEffect.getType().equals(PotionEffectType.SPEED) && activePotionEffect.getDuration() > (Integer.MAX_VALUE / 2))
                    player.removePotionEffect(PotionEffectType.SPEED);
            }

            giveEffect(player, item);
        }
    }

    public static void giveEffect(Player player, ItemStack item) {
        if (EnchantmentManager.getInstance().getEnchantmentLevel(item, EnchantmentManager.SONIC) == 1)
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));
        if (EnchantmentManager.getInstance().getEnchantmentLevel(item, EnchantmentManager.SONIC) == 2)
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.WHITE + "Constant Speed.",
                ChatColor.WHITE + "",
                ChatColor.DARK_PURPLE + "Attaches to Boots.",
                ChatColor.DARK_PURPLE + "Max Level: " + this.getMaxLevel()
        );
    }
}
