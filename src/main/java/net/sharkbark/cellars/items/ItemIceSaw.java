package net.sharkbark.cellars.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.BlockIceTFC;
import net.minecraft.block.Block;
import net.minecraft.block.BlockIce;
import net.minecraft.block.BlockPackedIce;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber()
public class ItemIceSaw extends ItemBase implements IMetalItem {
    private final Metal metal;
    private final double attackDamage;
    private final float attackSpeed;
    private final float efficiency;

    public ItemIceSaw(Metal metal, String name) {
        super(name);
        this.metal = metal;
        ToolMaterial material = metal.getToolMetal();
        setMaxStackSize(1);
        setMaxDamage(material.getMaxUses());
        efficiency = material.getEfficiency();
        attackDamage = (double)(0.5 * material.getAttackDamage());
        attackSpeed = -2.8F;
    }

    @Override
    public Size getSize(ItemStack itemStack) {
        return Size.LARGE;
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
    public boolean canStack(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean canHarvestBlock(IBlockState state, ItemStack stack)
    {        
        Block block = state.getBlock();
        if(block instanceof BlockIce || block instanceof BlockPackedIce || block instanceof BlockIceTFC) {
            return true;
        }
        return super.canHarvestBlock(state, stack);
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state)
    {
        return canHarvestBlock(state, stack) ? efficiency : 1.0f;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = HashMultimap.create();
        if (slot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", attackDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", attackSpeed, 0));
        }
        return multimap;
    }

    @Override
    public int getSmeltAmount(ItemStack itemStack) {
        if(isDamageable() && itemStack.isItemDamaged()) {
            double d = (itemStack.getMaxDamage() - itemStack.getItemDamage()) / (double) itemStack.getMaxDamage() - .10;
            return d < 0 ? 0 : MathHelper.floor((double)100 * d);
    
        }
        else {
            return 100;
        }
    }

    @Override
    public boolean onBlockDestroyed(ItemStack itemStack, World world, IBlockState state, BlockPos pos, EntityLivingBase player) {
        super.onBlockDestroyed(itemStack, world, state, pos, player);

        if(world.isRemote) {
            return false;
        }

        if (state.getBlockHardness(world, pos) > 0)
        {
            itemStack.damageItem(1, player);
        }

        if(canHarvestBlock(state, itemStack)) {
            EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, new ItemStack(state.getBlock(), 1));
            world.spawnEntity(entityItem);
            world.setBlockToAir(pos);
        }

        return true;
    }
}
