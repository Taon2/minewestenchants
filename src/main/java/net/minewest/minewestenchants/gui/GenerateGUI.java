package net.minewest.minewestenchants.gui;

import net.minewest.minewestenchants.MinewestEnchantsPlugin;
import net.minewest.minewestenchants.enchants.Enchantment;
import net.minewest.minewestenchants.enchants.EnchantmentManager;
import net.minewest.minewestenchants.listener.WorldListener;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GenerateGUI implements Listener {

    private Inventory menu;
    private String inventoryTitle = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Enchant Shop";
    private Map<Material, Integer> price = new HashMap<>();

    public GenerateGUI() {
        menu = Bukkit.createInventory(null, 27, inventoryTitle);
        fill();
        MinewestEnchantsPlugin.getInstance().getServer().getPluginManager()
                .registerEvents(this, MinewestEnchantsPlugin.getInstance());
        price.put(Material.DIAMOND, 10);
        price.put(Material.NETHERITE_INGOT, 4);
    }

    public void open(Player player) {
        player.openInventory(menu);
    }

    private void fill() {
        for (int i = 0; i < 9; i++) {
            menu.setItem(i, new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE, 1));
        }

        List<Enchantment> enchants = new ArrayList<>(EnchantmentManager.getInstance().getAll().values());
        for (int i = 9; i < 18; i++) {
            if (i % 2 == 0)
                menu.setItem(i, new ItemStack(Material.PURPLE_STAINED_GLASS_PANE, 1));
            else if (!enchants.isEmpty()) {
                ItemStack enchantItem = new ItemStack(Material.ENCHANTED_BOOK, 1);

                Map<Enchantment, Integer> toApply = new HashMap<>();
                toApply.put(enchants.get(0), 1);
                EnchantmentManager.getInstance().customEnchant(enchantItem, toApply);

                ItemMeta meta = enchantItem.getItemMeta();
                Enchantment enchant = enchants.get(0);

                if (meta != null) {
                    meta.setDisplayName(ChatColor.AQUA + enchant.getName() + " I");

                    List<String> lore;
                    if (!meta.hasLore())
                        lore = new ArrayList<>();
                    else
                        lore = meta.getLore();

                    lore.addAll(enchant.getDescription());
                    lore.add("");
                    lore.add(ChatColor.GOLD + "Cost:");
                    lore.add(ChatColor.WHITE + "- " + ChatColor.AQUA + "10 Diamonds");
                    lore.add(ChatColor.WHITE + "- " + ChatColor.DARK_GRAY + "4 Netherite Ingots");
                    lore.add(ChatColor.WHITE + "- " + ChatColor.GREEN + "40 XP Levels");

                    meta.setLore(lore);
                }
                enchants.remove(enchants.get(0));
                enchantItem.setItemMeta(meta);

                menu.setItem(i, enchantItem);
            }
        }

        for (int i = 18; i < 27; i++) {
            menu.setItem(i, new ItemStack(Material.MAGENTA_STAINED_GLASS_PANE, 1));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase(inventoryTitle)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        event.setCancelled(true);

        if (item != null) {
            if (!item.getType().equals(Material.ENCHANTED_BOOK)) {
                player.playSound(player.getLocation(),
                        Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                return;
            }

            if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

            if (!hasItems(player)) {
                player.sendMessage(ChatColor.RED + "You do not have enough items to purchase that enchantment.");
                player.playSound(player.getLocation(),
                        Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                return;
            }

            Map<Enchantment, Integer> enchants = EnchantmentManager.getInstance().getCustomEnchants(item);
            enchants.forEach(((enchantment, level) -> {
                player.getInventory().addItem(EnchantmentManager.getInstance().getEnchantedBook(enchantment, level));
            }));
            player.sendMessage(ChatColor.GREEN + "Enchantment purchased!");
            takeItems(player);
            player.getOpenInventory().close();
            player.playSound(player.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 1.0f, 1.0f);
            WorldListener.location.getWorld().spawnParticle(Particle.REVERSE_PORTAL, WorldListener.location, 200);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        HandlerList.unregisterAll(this);
    }

    private boolean hasItems(Player player) {
        Inventory inventory = player.getInventory();
        boolean hasDiamonds = inventory.containsAtLeast(new ItemStack(Material.DIAMOND), 10);
        boolean hasNetherite = inventory.containsAtLeast(new ItemStack(Material.NETHERITE_INGOT), 4);
        boolean hasLevels = player.getLevel() >= 40;

        return hasDiamonds && hasNetherite && hasLevels;
    }

    private void takeItems(Player player) {
        Inventory inventory = player.getInventory();

        Map<Material, Integer> toTake = price;
        for (ItemStack content : inventory.getContents()) {
            if (content == null) continue;

            if (toTake.keySet().contains(content.getType())) {
                int required = toTake.get(content.getType());
                if (content.getAmount() > required) {
                    content.setAmount(content.getAmount() - required);
                } else {
                    inventory.remove(content);
                }

                List<Material> toRemove = new ArrayList<>();
                toTake.forEach((item, amount) -> {
                    if (amount <= 0) {
                        toRemove.add(item);
                    }
                });

                if (!toRemove.isEmpty()) {
                    for (Material material : toRemove) {
                        toTake.remove(material);
                    }
                }
            }
        }
        player.setLevel((player.getLevel() - 40));
        player.updateInventory();
    }
}
