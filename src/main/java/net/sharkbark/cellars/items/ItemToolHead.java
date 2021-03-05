package net.sharkbark.cellars.items;

import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.minecraft.item.ItemStack;

public class ItemToolHead extends ItemBase implements IMetalItem {
    private final Metal metal;

    public ItemToolHead(Metal metal, String name, String oreName) {
        super(name);
        this.metal = metal;
    }

    @Override
    public Size getSize(ItemStack arg0) {
        return Size.NORMAL;
    }

    @Override
    public Weight getWeight(ItemStack itemStack) {
        return Weight.MEDIUM;
    }

    @Override
    public Metal getMetal(ItemStack itemStack) {
        return metal;
    }

    @Override
    public int getSmeltAmount(ItemStack itemStack) {
        return 100;
    }
}