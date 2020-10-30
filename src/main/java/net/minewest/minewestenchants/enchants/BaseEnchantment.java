package net.minewest.minewestenchants.enchants;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEnchantment implements Enchantment {
    private final String name;
    private final int minLevel;
    private final int maxLevel;
    private final String handle;

    BaseEnchantment(String handle, String name, int minLevel, int maxLevel) {
        this.handle = handle;
        this.name = name;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;

        EnchantmentManager.getInstance().registerEnchantment(this);
        postLoad();
    }

    protected void postLoad() {
    }

    @Override
    public String getHandle() {
        return handle;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMinLevel() {
        return minLevel;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public List<String> getDescription() {
        return new ArrayList<>();
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
