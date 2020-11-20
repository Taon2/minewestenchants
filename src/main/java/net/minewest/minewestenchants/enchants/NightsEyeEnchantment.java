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

public class NightsEyeEnchantment extends BaseEnchantment {
    NightsEyeEnchantment() {
        super("NIGHTSEYE", "Night's Eye", 1, 1);
    }

    @Override
    protected void postLoad() {
        super.postLoad();
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return item.getType().name().contains("HELMET");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = event.getPlayer();
                if (player == null || !player.isOnline()) return;

                ItemStack item = player.getInventory().getHelmet();
                if (item != null && EnchantmentManager.getInstance().getEnchantmentLevel(item, EnchantmentManager.NIGHTS_EYE) > 0) {

                    for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                        if (activePotionEffect.getType().equals(PotionEffectType.NIGHT_VISION) && activePotionEffect.getDuration() > (Integer.MAX_VALUE / 2))
                            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    }

                    giveEffect(player, item);
                } else {
                    for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                        if (activePotionEffect.getType().equals(PotionEffectType.NIGHT_VISION) && activePotionEffect.getDuration() > (Integer.MAX_VALUE / 2))
                            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                    }
                }
            }
        }.runTaskLater(MinewestEnchantsPlugin.getInstance(), 2L);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();

        if (event.getType() != ArmorType.HELMET) return;

        //Checks for old helmet with nights eye being removed
        if (event.getOldArmorPiece() != null && EnchantmentManager.getInstance().getEnchantmentLevel(event.getOldArmorPiece(), EnchantmentManager.NIGHTS_EYE) > 0) {
            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                if (activePotionEffect.getType().equals(PotionEffectType.NIGHT_VISION) && activePotionEffect.getDuration() > (Integer.MAX_VALUE / 2))
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
        } else {
            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                if (activePotionEffect.getType().equals(PotionEffectType.NIGHT_VISION) && activePotionEffect.getDuration() > (Integer.MAX_VALUE / 2))
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }
        }
        //Checks for new boots with nights eye
        if (event.getNewArmorPiece() != null && EnchantmentManager.getInstance().getEnchantmentLevel(event.getNewArmorPiece(), EnchantmentManager.NIGHTS_EYE) > 0) {
            ItemStack item = event.getNewArmorPiece();

            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                if (activePotionEffect.getType().equals(PotionEffectType.NIGHT_VISION) && activePotionEffect.getDuration() > (Integer.MAX_VALUE / 2))
                    player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            }

            giveEffect(player, item);
        }
    }

    public static void giveEffect(Player player, ItemStack item) {
        if (EnchantmentManager.getInstance().getEnchantmentLevel(item, EnchantmentManager.NIGHTS_EYE) == 1)
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.WHITE + "Constant Night Vision.",
                ChatColor.WHITE + "",
                ChatColor.DARK_PURPLE + "Attaches to Helmets.",
                ChatColor.DARK_PURPLE + "Max Level: " + EnchantmentManager.getInstance().getRomanNumerals(this.getMaxLevel())
        );
    }
}
