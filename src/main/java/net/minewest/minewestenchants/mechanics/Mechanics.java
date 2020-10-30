package net.minewest.minewestenchants.mechanics;

import net.minewest.minewestenchants.MinewestEnchantsPlugin;

import java.util.Arrays;

public class Mechanics {
    private static EnchantMechanics current;

    public static void init() {

        String conf = MinewestEnchantsPlugin.getInstance().getConfig().getString("enchant-mechanics", "CURRENT").toUpperCase();

        for (EnchantMechanics mechanics : Arrays.asList(
                new OriginalMechanics()
        )) {
            if (mechanics.getMechanicsName().equalsIgnoreCase(conf)) current = mechanics;
        }

        if (current == null) current = new OriginalMechanics();
    }

    public static EnchantMechanics get() {
        return current;
    }
}
