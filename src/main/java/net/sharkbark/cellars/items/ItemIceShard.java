package net.sharkbark.cellars.items;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.minecraft.item.ItemStack;

public class ItemIceShard extends ItemBase {

    public ItemIceShard(String name) {
        super(name);
    }

    @Override
    public Size getSize(ItemStack stack) {
        return Size.SMALL;
    }

    @Override
    public Weight getWeight(ItemStack stack) {
        return Weight.LIGHT;
    }
}
