package net.sharkbark.cellars.blocks;

import net.dries007.tfc.util.Helpers;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.sharkbark.cellars.Main;
import net.sharkbark.cellars.init.ModBlocks;
import net.sharkbark.cellars.init.ModItems;
import net.sharkbark.cellars.blocks.tileentity.TECellarShelf;
import net.sharkbark.cellars.util.IHasModel;
import net.sharkbark.cellars.util.Reference;

import javax.annotation.Nullable;

public class BlockCellarShelf extends BlockContainer implements IHasModel {

    public BlockCellarShelf(String name, Material material) {
        super(Material.WOOD);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(Main.creativeTab);
        setHardness(2F);
        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(name));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing playerFacing, float hitX, float hitY, float hitZ) {

        if(!worldIn.isRemote){
            player.openGui(Main.INSTANCE, Reference.GUI_CELLAR_SHELF, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;

    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if(stack.hasDisplayName()){
            TileEntity entity = worldIn.getTileEntity(pos);

            if(entity instanceof TECellarShelf){
                //((TECellarShelf)entity).setCustomName(stack.getDisplayName());
            }
        }

    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TECellarShelf tile = Helpers.getTE(worldIn, pos, TECellarShelf.class);
        if (tile != null)
        {
            tile.onBreakBlock(worldIn, pos, state);
        }
        super.breakBlock(worldIn, pos, state);
    }



    @Nullable
    @Override
    public TileEntity createNewTileEntity(World world, int i) {
        if(!world.isRemote) {
            System.out.println("Client : Creating TileEntity");
        }else{
            System.out.println("Server : Creating TileEntity");
        }
        return new TECellarShelf();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean isOpaqueCube(IBlockState p_isOpaqueCube_1_) {
        return false;
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(Item.getItemFromBlock(this),0,"inventory");
    }


}
