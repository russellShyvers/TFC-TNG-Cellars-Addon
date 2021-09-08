package net.sharkbark.cellars.blocks;

import net.dries007.tfc.objects.CreativeTabsTFC;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.sharkbark.cellars.Main;
import net.sharkbark.cellars.blocks.itemblocks.ItemBlockDoor;
import net.sharkbark.cellars.init.ModBlocks;
import net.sharkbark.cellars.init.ModItems;
import net.sharkbark.cellars.util.IHasModel;

import java.util.Random;

public class DoorBase extends BlockDoor implements IHasModel {

    protected DoorBase(String name, Material material) {
        //setUnlocalizedName(name);
        super(material);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(Main.creativeTab);
        setHardness(2F);
        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlockDoor(this).setRegistryName(this.getRegistryName()));
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? Items.AIR : Item.getItemFromBlock(this);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World worldIn, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this);
    }

    @Override
    public void registerModels() {

        Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");

    }
}
