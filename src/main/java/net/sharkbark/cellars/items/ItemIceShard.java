package net.sharkbark.cellars.items;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.minecraft.item.ItemStack;
import net.sharkbark.cellars.Main;

public class ItemIceShard extends ItemBase {

    public ItemIceShard(String name) {
        super(name);
        setCreativeTab(Main.creativeTab);
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
