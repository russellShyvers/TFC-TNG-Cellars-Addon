package net.sharkbark.cellars.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.sharkbark.cellars.ModConfig;
import net.sharkbark.cellars.blocks.tileentity.TEInfectedAir;

public class CellarWall extends BlockBase {

    public CellarWall(String name, Material material) {
        super(name, material);
    }

}
