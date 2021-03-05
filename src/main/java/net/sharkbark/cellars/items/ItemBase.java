package net.sharkbark.cellars.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.sharkbark.cellars.Main;
import net.sharkbark.cellars.init.ModItems;
import net.sharkbark.cellars.util.IHasModel;

public class ItemBase extends Item implements IHasModel {

    public ItemBase(String name){
        //setUnlocalizedName(name);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(CreativeTabs.MATERIALS);

        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }

}
