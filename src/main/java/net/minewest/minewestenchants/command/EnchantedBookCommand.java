package net.minewest.minewestenchants.command;

import net.minewest.minewestenchants.enchants.Enchantment;
import net.minewest.minewestenchants.enchants.EnchantmentManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantedBookCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("skycade.enchants.enchantedbook")) {
            return true;
        }

        if (args == null || args.length < 3) {
            sender.sendMessage("Usage: /enchantedbook <player> <enchantment> <level>");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("No such player!");
            return true;
        }

        String ench = args[1];

        Object enchant = EnchantmentManager.getInstance().getEnchantmentByName(ench);

        if (enchant == null) {
            sender.sendMessage(ChatColor.RED + "Enchant not found.");
            return true;
        }

        String num = args[2];
        int level;
        try {
            level = Integer.parseInt(num);
        } catch (NullPointerException | NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid level.");
            return true;
        }

        ItemStack item = null;

        int max;
        if (enchant instanceof Enchantment) {
            max = ((Enchantment) enchant).getMaxLevel();
            if (max < level || level < ((Enchantment) enchant).getMinLevel()) {
                sender.sendMessage(ChatColor.RED + "Invalid level! Must be between " +
                        ChatColor.AQUA + ((Enchantment) enchant).getMinLevel() +
                        ChatColor.RED + " and " + ChatColor.AQUA + max + ChatColor.RED + ".");
                return true;
            }
            item = EnchantmentManager.getInstance().getEnchantedBook(((Enchantment) enchant), level);
        }

        if (item != null)
            player.getInventory().addItem(item);

        player.sendMessage(ChatColor.GREEN + "Done!");

        return true;
    }
}
