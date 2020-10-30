package net.minewest.minewestenchants.enchants;

import net.minewest.minewestenchants.MinewestEnchantsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class AutoFeedEnchantment extends BaseEnchantment {
    private static final MinewestEnchantsPlugin plugin = JavaPlugin.getPlugin(MinewestEnchantsPlugin.class);
    private Map<UUID, Long> lastMove = new HashMap<>();

    AutoFeedEnchantment() {
        super("AUTOFEED", "Autofeed", 1, 1);
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return item.getType().name().contains("HELMET");
    }

    @Override
    protected void postLoad() {
        super.postLoad();
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Long> entry : lastMove.entrySet()) {
                    if ((System.currentTimeMillis() - entry.getValue()) / 1000L <= 2L) {
                        Player player = Bukkit.getPlayer(entry.getKey());
                        if (player == null) continue;
                        ItemStack item = player.getInventory().getHelmet();
                        if (item == null || EnchantmentManager.getInstance().getEnchantmentLevel(item, EnchantmentManager.AUTOFEED) <= 0) continue;

                        int level = EnchantmentManager.getInstance().getEnchantmentLevel(item, EnchantmentManager.AUTOFEED);
                        if (player.getFoodLevel() != 20)
                            player.setFoodLevel(player.getFoodLevel() + level);
                    }
                }
            }
        }.runTaskTimer(plugin, 100L, 100L);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlock().equals(event.getTo().getBlock())) return;

        lastMove.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        lastMove.remove(event.getPlayer().getUniqueId());
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.WHITE + "Feeds you as you move around.",
                ChatColor.WHITE + "",
                ChatColor.DARK_PURPLE + "Attaches to Helmets.",
                ChatColor.DARK_PURPLE + "Max Level: " + this.getMaxLevel()
        );
    }
}
