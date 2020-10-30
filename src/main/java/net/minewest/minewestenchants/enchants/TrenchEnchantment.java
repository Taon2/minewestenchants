package net.minewest.minewestenchants.enchants;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.*;

public class TrenchEnchantment extends BaseEnchantment {
    private static Set<Material> noTrench;
    private Map<UUID, BlockFace> lastClickedBlockface = new HashMap<>();

    static {
        noTrench = new HashSet<>(Arrays.asList(
                Material.BEDROCK,
                Material.OBSIDIAN,
                Material.ENCHANTING_TABLE,
                Material.BARRIER,
                Material.ENDER_CHEST,
                Material.TRAPPED_CHEST,
                Material.CHEST,
                Material.WATER,
                Material.LAVA,
                Material.SPAWNER,
                Material.END_PORTAL_FRAME,
                Material.END_PORTAL,
                Material.NETHER_PORTAL));
    }

    TrenchEnchantment() {
        super("TRENCH", "Trench", 1, 1);
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return item.getType().name().contains("PICKAXE") || item.getType().name().contains("SHOVEL");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        lastClickedBlockface.put(event.getPlayer().getUniqueId(), event.getBlockFace());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event instanceof TrenchEnchantment.TrenchBlockBreakEvent) return;

        ItemStack item = event.getPlayer().getInventory().getItemInHand();
        if (item == null || EnchantmentManager.getInstance().getEnchantmentLevel(item, EnchantmentManager.TRENCH) <= 0) return;
        if (!lastClickedBlockface.containsKey(event.getPlayer().getUniqueId())) return;

        Block block = event.getBlock();

        TrenchBlockBreakEvent event1 = new TrenchBlockBreakEvent(block, event.getPlayer());
        Bukkit.getPluginManager().callEvent(event1);

        BlockVector bv = block.getLocation().toVector().toBlockVector();
        World world = block.getWorld();
        int radius = 2;
        int i, j, k;

        BlockFace lastBlockFace = lastClickedBlockface.get(event.getPlayer().getUniqueId());
        if (lastBlockFace == BlockFace.DOWN
                || lastBlockFace == BlockFace.UP) {
            for (i = -radius; i <= radius; ++i) {
                for (k = -radius; k <= radius; ++k) {
                    if (i == 0 && k == 0) continue;

                    Vector vector = new Vector(i, 0, k); // the vector!
                    Vector add = bv.clone().add(vector);
                    Block b = add.toLocation(world).getBlock();

                    if (!isTrenchBreakable(b.getType())) continue;

                    TrenchBlockBreakEvent event2 = new TrenchBlockBreakEvent(b, event.getPlayer());
                    Bukkit.getPluginManager().callEvent(event2);

                    if (!event1.isCancelled()) {
                        b.breakNaturally(item);
                    }
                }
            }
        } else if (lastBlockFace == BlockFace.NORTH
                || lastBlockFace == BlockFace.SOUTH){
            for (i = -radius; i <= radius; ++i) {
                for (j = -radius; j <= radius; ++j) {
                    if (i == 0 && j == 0) continue;

                    Vector vector = new Vector(i, j, 0); // the vector!
                    Vector add = bv.clone().add(vector);
                    Block b = add.toLocation(world).getBlock();

                    if (!isTrenchBreakable(b.getType())) continue;

                    TrenchBlockBreakEvent event2 = new TrenchBlockBreakEvent(b, event.getPlayer());
                    Bukkit.getPluginManager().callEvent(event2);

                    if (!event1.isCancelled()) {
                        b.breakNaturally(item);
                    }
                }
            }
        } else if (lastBlockFace == BlockFace.EAST
                || lastBlockFace == BlockFace.WEST){
            for (j = -radius; j <= radius; ++j) {
                for (k = -radius; k <= radius; ++k) {
                    if (j == 0 && k == 0) continue;

                    Vector vector = new Vector(0, j, k); // the vector!
                    Vector add = bv.clone().add(vector);
                    Block b = add.toLocation(world).getBlock();

                    if (!isTrenchBreakable(b.getType())) continue;

                    TrenchBlockBreakEvent event2 = new TrenchBlockBreakEvent(b, event.getPlayer());
                    Bukkit.getPluginManager().callEvent(event2);

                    if (!event1.isCancelled()) {
                        b.breakNaturally(item);
                    }
                }
            }
        }
    }

    private static boolean isTrenchBreakable(Material material) {
        return !noTrench.contains(material);
    }

    public static class TrenchBlockBreakEvent extends BlockBreakEvent {
        TrenchBlockBreakEvent(Block theBlock, Player player) {
            super(theBlock, player);
        }
    }

    @Override
    public List<String> getDescription() {
        return Arrays.asList(
                ChatColor.WHITE + "Breaks blocks in a radius",
                ChatColor.WHITE + "from where you mined.",
                ChatColor.WHITE + "",
                ChatColor.DARK_PURPLE + "Attaches to Pickaxes and Shovels.",
                ChatColor.DARK_PURPLE + "Max Level: " + this.getMaxLevel()
        );
    }
}
