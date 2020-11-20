package net.minewest.minewestenchants.listener;

import net.minewest.minewestenchants.MinewestEnchantsPlugin;
import net.minewest.minewestenchants.gui.GenerateGUI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {

    public static Location location;

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (!event.getWorld().getName().equals(MinewestEnchantsPlugin.getInstance().getConfig().getString("generator.world"))) return;

        location = new Location(
                event.getWorld(),
                MinewestEnchantsPlugin.getInstance().getConfig().getInt("generator.x") + 0.5,
                MinewestEnchantsPlugin.getInstance().getConfig().getInt("generator.y") + 0.5,
                MinewestEnchantsPlugin.getInstance().getConfig().getInt("generator.z") + 0.5);

        Bukkit.getScheduler().runTaskTimer(MinewestEnchantsPlugin.getInstance(), () -> {
            location.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, location, 1, 1, 1, 1, 0.0f);
        }, 1L ,1L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || !block.getType().equals(Material.ENCHANTING_TABLE)) return;

        if (isGenerator(block)) {
            event.setCancelled(true);
            new GenerateGUI().open(event.getPlayer());
            event.getPlayer().playSound(event.getPlayer().getLocation(),
                    Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!block.getType().equals(Material.ENCHANTING_TABLE)) return;
        if (isGenerator(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    private boolean isGenerator(Block block) {
        return block.getWorld().getName().equals(MinewestEnchantsPlugin.getInstance().getConfig().getString("generator.world")) &&
                block.getX() == MinewestEnchantsPlugin.getInstance().getConfig().getInt("generator.x") &&
                block.getY() == MinewestEnchantsPlugin.getInstance().getConfig().getInt("generator.y") &&
                block.getZ() == MinewestEnchantsPlugin.getInstance().getConfig().getInt("generator.z");
    }
}
