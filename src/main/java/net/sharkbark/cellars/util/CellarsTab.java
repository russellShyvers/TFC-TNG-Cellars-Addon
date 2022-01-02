package net.sharkbark.cellars.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.sharkbark.cellars.init.ModBlocks;

public class CellarsTab extends CreativeTabs{

    public CellarsTab() {
        super(Reference.MOD_ID);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(ModBlocks.CELLAR_DOOR);
    }

}
