package net.minewest.minewestenchants.enchants;

import net.minewest.minewestenchants.MinewestEnchantsPlugin;
import net.minewest.minewestenchants.mechanics.EnchantMechanics;
import net.minewest.minewestenchants.mechanics.Mechanics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static org.bukkit.event.EventPriority.HIGH;
import static org.bukkit.event.EventPriority.LOWEST;

public class EnchantmentManager implements Listener {

    public static final Enchantment AUTOFEED = new AutoFeedEnchantment();
    public static final Enchantment EXPERIENCED = new ExperiencedEnchantment();
    public static final Enchantment NIGHTS_EYE = new NightsEyeEnchantment();
    public static final Enchantment SONIC = new SonicEnchantment();
    public static final Enchantment TRENCH = new TrenchEnchantment();

    private static EnchantmentManager enchantmentManager;
    private Map<String, Enchantment> map = new HashMap<>();
    public static final String MAGIC = "§1§2§3§7";

    public EnchantmentManager() {
        enchantmentManager = this;
        MinewestEnchantsPlugin.getInstance().getServer().getPluginManager()
                .registerEvents(this, MinewestEnchantsPlugin.getInstance());
    }

    void registerEnchantment(Enchantment enchantment) {
        if (map == null) map = new HashMap<>();
        map.put(enchantment.getHandle(), enchantment);
        MinewestEnchantsPlugin.getInstance().getServer().getPluginManager()
                .registerEvents(enchantment, MinewestEnchantsPlugin.getInstance());
    }

    @EventHandler(ignoreCancelled = true, priority = HIGH)
    public void on(InventoryClickEvent event) {
        if (event.getClickedInventory() == null || event.getCurrentItem() == null) return;
        if (getCustomEnchants(event.getCurrentItem()).size() != 0
                || (event.getCurrentItem().getType().equals(Material.ENCHANTED_BOOK)
                && ((EnchantmentStorageMeta) event.getCurrentItem().getItemMeta()).getStoredEnchants().size() != 0))
            reformat(event.getCurrentItem());
    }

    @EventHandler(ignoreCancelled = true, priority = LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getClick().equals(ClickType.LEFT)) return;

        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;

        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        ItemStack cursor = event.getCursor();
        ItemStack item = event.getClickedInventory().getItem(event.getSlot());

        if (cursor == null || !cursor.getType().equals(Material.ENCHANTED_BOOK) || item == null) return;

        Map<Enchantment, Integer> customEnchants = getCustomEnchants(cursor);

        if (customEnchants.size() <= 0) return;

        EnchantMechanics mechanics = Mechanics.get();

        // stack different enchanted books
        if (item.getType().equals(Material.ENCHANTED_BOOK)) {

            for (Map.Entry<Enchantment, Integer> entry : customEnchants.entrySet()) {
                int result = mechanics.getBookResult(item, entry.getKey(), entry.getValue());
                if (result < 0) {
                    player.sendMessage(ChatColor.RED + "You cannot combine these two items!");
                    return;
                }
                entry.setValue(result);
            }

            customEnchantUnsafe(item, customEnchants);

            event.getView().setCursor(null);
            player.updateInventory();

            player.sendMessage(ChatColor.GREEN + "Success!");
            return;

        }

        // removes custom enchants that arent allowed on the item
        List<Map.Entry<Enchantment, Integer>> toRemoveCustom = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> entry : customEnchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (!enchantment.canEnchantItem(item) && item.getType() != Material.ENCHANTED_BOOK) {
                toRemoveCustom.add(entry);
            }
        }
        toRemoveCustom.forEach(customEnchants.entrySet()::remove);

        for (Map.Entry<Enchantment, Integer> entry : customEnchants.entrySet()) {
            int result = mechanics.getItemResult(item, entry.getKey(), entry.getValue());
            if (result < 0) {
                player.sendMessage(ChatColor.RED + "You cannot combine these two items!");
                return;
            }
            entry.setValue(result);
        }

        // determine if the enchanted book and the item are not allowed to be combined
        for (Map.Entry<Enchantment, Integer> entry : customEnchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (!enchantment.canEnchantItem(item) && item.getType() != Material.ENCHANTED_BOOK) {
                player.sendMessage(ChatColor.RED + "You cannot combine these two items!");
                return;
            }
        }

        if (!customEnchants.isEmpty()) {
            customEnchant(item, customEnchants);
        } else {
            player.sendMessage(ChatColor.RED + "You cannot enchant that item with that enchantment.");
            event.setCancelled(true);
            return;
        }

        event.getView().setCursor(null);
        player.updateInventory();

        player.sendMessage(ChatColor.GREEN + "Success!");
    }

    public void reformat(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().equals(Material.AIR)) return;

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());

        Map<Enchantment, Integer> customEnchants = getCustomEnchants(itemStack);

        Map<String, String> loreEnchantments = new TreeMap<>();

        for (Map.Entry<Enchantment, Integer> entry : customEnchants.entrySet()) {
            loreEnchantments.put(MAGIC + entry.getKey().getName(), getRomanNumerals(entry.getValue()));
        }

        List<String> newLore = new ArrayList<>();
        for (Map.Entry<String, String> entry : loreEnchantments.entrySet()) {
            newLore.add(entry.getKey() + " " + entry.getValue());
        }

        itemMeta.setLore(newLore);
        itemStack.setItemMeta(itemMeta);
    }

    public String getRomanNumerals(int level) {
        switch (level) {
            case 1:
                return "I";
            case 2:
                return "II";
            case 3:
                return "III";
            case 4:
                return "IV";
            case 5:
                return "V";
            default:
                // if you get more than 5 you're insane
                return "";
        }
    }

    public int getNumFromRoman(String roman) {
        switch (roman) {
            case "I":
                return 1;
            case "II":
                return 2;
            case "III":
                return 3;
            case "IV":
                return 4;
            case "V":
                return 5;
            default:
                // if you get more than 5 you're insane
                return 0;
        }
    }

    public Map<Enchantment, Integer> getCustomEnchants(ItemStack itemStack) {
        Map<Enchantment, Integer> enchants = new HashMap<>();

        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) {
            return enchants;
        }

        for (String s : itemStack.getItemMeta().getLore()) {
            String line = ChatColor.stripColor(s);

            for (Enchantment enchantment : map.values()) {
                if (line.lastIndexOf(" ") == -1) continue;

                if (line.substring(0, line.lastIndexOf(" ")).equals(enchantment.getName())) {
                    int level = getNumFromRoman(line.substring(line.lastIndexOf(" ") + 1));

                    if (level > 0) enchants.put(enchantment, level);
                }
            }
        }

        return enchants;
    }

    public int getEnchantmentLevel(ItemStack itemStack, Enchantment enchantment) {
        if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasLore()) {
            return 0;
        }

        for (String s : itemStack.getItemMeta().getLore()) {
            String line = ChatColor.stripColor(s);

            if (line.substring(0, line.lastIndexOf(" ")).equals(enchantment.getName())) {
                int level = getNumFromRoman(line.substring(line.lastIndexOf(" ") + 1));

                if (level > 0) return level;
            }
        }

        return 0;
    }

    public Enchantment getEnchantmentByName(String name) {
        Enchantment customEnchant = map.getOrDefault(name.toUpperCase(), null);

        return customEnchant;
    }

    public boolean customEnchant(ItemStack itemStack, Map<Enchantment, Integer> enchants) {
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            Integer level = entry.getValue();
            if (itemStack == null || enchantment == null || (!enchantment.canEnchantItem(itemStack) && itemStack.getType() != Material.ENCHANTED_BOOK)
                    || enchantment.getMinLevel() > level || enchantment.getMaxLevel() < level)
                continue;
        }
        return customEnchantUnsafe(itemStack, enchants);
    }

    public boolean customEnchantUnsafe(ItemStack itemStack, Map<Enchantment, Integer> enchants) {

        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();

        enchants.forEach((enchantment, level) -> {
            if (getEnchantmentLevel(itemStack, enchantment) > level) {
                return;
            }

            List<String> toRemove = new ArrayList<>();
            for (String s : lore) {
                String line = ChatColor.stripColor(s);
                if (line.substring(0, line.lastIndexOf(" ")).equals(enchantment.getName())) {
                    toRemove.add(s);
                }
            }
            lore.removeAll(toRemove);

            lore.add(0, MAGIC + enchantment.getName() + " " + getRomanNumerals(level));
        });

        reformat(itemStack);

        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);

        return true;
    }

    public ItemStack getEnchantedBook(Enchantment enchantment, Integer level) {
        List<String> lore = new ArrayList<>();

        lore.add(MAGIC + enchantment.getName() + " " + getRomanNumerals(level));

        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        reformat(itemStack);

        return itemStack;
    }

    public Map<String, Enchantment> getAll() {
        return new HashMap<>(map);
    }

    public static EnchantmentManager getInstance() {
        if (enchantmentManager == null) {
            enchantmentManager = new EnchantmentManager();
        }

        return enchantmentManager;
    }
}
