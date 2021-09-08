package net.sharkbark.cellars.blocks;

import net.dries007.tfc.util.Helpers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.sharkbark.cellars.Main;
import net.sharkbark.cellars.blocks.tileentity.TEFreezeDryer;
import net.sharkbark.cellars.init.ModBlocks;
import net.sharkbark.cellars.init.ModItems;
import net.sharkbark.cellars.util.IHasModel;
import net.sharkbark.cellars.util.Reference;
import scala.xml.MetaData;

import javax.annotation.Nullable;
import java.util.Random;

public class FreezeDryer extends BlockContainer implements IHasModel {

    public static final PropertyDirection FACING;

    static {
        FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    }

    public FreezeDryer(String name, Material material) {
        super(Material.ROCK);
        setTranslationKey(name);
        setRegistryName(name);
        setCreativeTab(Main.creativeTab);
        setHardness(2F);
        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(name));
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {

        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    public EnumFacing getFacing(IBlockState state){
        return state.getValue(FACING);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        this.setDefaultFacing(worldIn, pos, state);
    }

    private void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.north());
            IBlockState iblockstate1 = worldIn.getBlockState(pos.south());
            IBlockState iblockstate2 = worldIn.getBlockState(pos.west());
            IBlockState iblockstate3 = worldIn.getBlockState(pos.east());
            EnumFacing enumfacing = state.getValue(FACING);

            if (enumfacing == EnumFacing.NORTH && iblockstate.isFullBlock() && !iblockstate1.isFullBlock())
            {
                enumfacing = EnumFacing.SOUTH;
            }
            else if (enumfacing == EnumFacing.SOUTH && iblockstate1.isFullBlock() && !iblockstate.isFullBlock())
            {
                enumfacing = EnumFacing.NORTH;
            }
            else if (enumfacing == EnumFacing.WEST && iblockstate2.isFullBlock() && !iblockstate3.isFullBlock())
            {
                enumfacing = EnumFacing.EAST;
            }
            else if (enumfacing == EnumFacing.EAST && iblockstate3.isFullBlock() && !iblockstate2.isFullBlock())
            {
                enumfacing = EnumFacing.WEST;
            }

            worldIn.setBlockState(pos, state.withProperty(FACING, enumfacing), 2);
        }
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING});
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.byHorizontalIndex(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y)
        {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }


    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return ((EnumFacing)state.getValue(FACING)).getIndex();
    }

    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing playerFacing, float hitX, float hitY, float hitZ) {

        if(!worldIn.isRemote){
            player.openGui(Main.INSTANCE, Reference.GUI_FREEZE_DRYER, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }

        return true;

    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {

        if(stack.hasDisplayName()){
            TileEntity entity = worldIn.getTileEntity(pos);

            if(entity instanceof TEFreezeDryer){
                //((TECellarShelf)entity).setCustomName(stack.getDisplayName());
            }
        }

    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TEFreezeDryer tile = Helpers.getTE(worldIn, pos, TEFreezeDryer.class);
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
        return new TEFreezeDryer();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;

    }



    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(Item.getItemFromBlock(this),0,"inventory");
    }
}
