package net.sharkbark.cellars.blocks.tileentity;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.objects.inventory.capability.IItemHandlerSidedCallback;
import net.dries007.tfc.objects.inventory.capability.ItemHandlerSidedWrapper;
import net.dries007.tfc.objects.te.TEInventory;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.sharkbark.cellars.ModConfig;
import net.sharkbark.cellars.util.Reference;

import javax.annotation.Nullable;

import static net.sharkbark.cellars.blocks.FreezeDryer.FACING;

public class TEFreezeDryer extends TEInventory implements IItemHandlerSidedCallback, ITickable {

    boolean lastTick = false;
    private float temperature = 1;
    private int lastUpdate = 0;
    private int tick = 0;
    private float pressure = 0;
    private int powerLevel = 0;
    private float coolant = 0;
    private boolean initialized = false;
    private float localTemperature = 0;
    private float localPressure = 0;
    private boolean overheating = false;

    public TEFreezeDryer() {
        super(new TEFreezeDryer.FreezeDryerItemStackHandler(9));
    }



    @Override
    public void update() {

        if(!initialized){
            localTemperature = ClimateTFC.getActualTemp(this.getPos());
            temperature = localTemperature;
            localPressure = ModConfig.seaLevelPressure + ((-(this.getPos().getY()-ModConfig.seaLevel)) * ModConfig.pressureChange);
            pressure = localPressure;
            initialized = !initialized;
        }
        
        if((++tick)%100 != 0){
            return;
        }
        tick = 0;

        EnumFacing facing = world.getBlockState(this.getPos()).getValue(FACING);

        if(world.isBlockPowered(this.getPos())){
            if(EnumFacing.NORTH == facing) {
                powerLevel = world.getRedstonePower(getPos().offset(EnumFacing.SOUTH), EnumFacing.SOUTH);
            } else if(EnumFacing.EAST == facing) {
                powerLevel = world.getRedstonePower(getPos().offset(EnumFacing.WEST), EnumFacing.WEST);
            } else if(EnumFacing.SOUTH == facing) {
                powerLevel = world.getRedstonePower(getPos().offset(EnumFacing.NORTH), EnumFacing.NORTH);
            } else if(EnumFacing.WEST == facing) {
                powerLevel = world.getRedstonePower(getPos().offset(EnumFacing.EAST), EnumFacing.EAST);
            }
        } else {
            powerLevel = 0;
        }

        localTemperature = ClimateTFC.getActualTemp(this.getPos());

        //Dissipate Heat
        temperature = temperature + ModConfig.temperatureDissipation*(localTemperature - temperature);

        if(world.isBlockPowered(this.getPos())) {
            //Increase heat
            if(temperature < 50){
                overheating = false;
                temperature = temperature + (ModConfig.heatPerPower * powerLevel);

            //"Explode"
            } else {
                overheating = true;
                world.spawnParticle(EnumParticleTypes.CRIT, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, 1, 1, 1);
            }

            //decrease pressure
            pressure = pressure - (powerLevel * ModConfig.workPerPower);

            if (!world.isRemote) {
                if (EnumFacing.NORTH == facing) {
                    world.spawnParticle(EnumParticleTypes.WATER_DROP, this.pos.getX() + 0.7, this.pos.getY() + 0.6, this.pos.getZ() + 1, 0, 0.1, 0);
                } else if (EnumFacing.EAST == facing) {
                    world.spawnParticle(EnumParticleTypes.WATER_DROP, this.pos.getX() + 0, this.pos.getY() + 0.6, this.pos.getZ() + 0.7, 0, 0.1, 0);
                } else if (EnumFacing.SOUTH == facing) {
                    world.spawnParticle(EnumParticleTypes.WATER_DROP, this.pos.getX() + 0.3, this.pos.getY() + 0.6, this.pos.getZ() + 0, 0, 0.1, 0);
                } else if (EnumFacing.WEST == facing) {
                    world.spawnParticle(EnumParticleTypes.WATER_DROP, this.pos.getX() + 1, this.pos.getY() + 0.6, this.pos.getZ() + 0.3, 0, 0.1, 0);
                }
            }
        }
    }



    private void handleItemTicking() {
        // Handle dryer ticks
    }
    private String getTrait(ItemStack stack, NBTTagCompound nbt){
        String string = nbt.getString("CellarAddonTemperature");
        if(string.compareTo("cool") == 0){
            return "cool";
        }
        if(string.compareTo("icy") == 0){
            return "icy";
        }
        if(string.compareTo("freezing") == 0){
            return "freezing";
        }
        if(string.compareTo("freeze dryed") == 0){
            return "freeze dryed";
        }
        return "";
    }
    private void applyTrait(ItemStack stack, NBTTagCompound nbt, String string, FoodTrait trait){
        nbt.setString("CellarAddonTemperature", string);
        CapabilityFood.applyTrait(stack, trait);
        stack.setTagCompound(nbt);
    }
    private void updateTraits() {
        for (int x = 0; x < inventory.getSlots(); x++) {
            ItemStack stack = inventory.getStackInSlot(x);
            NBTTagCompound nbt;
            if(stack.hasTagCompound()){
                nbt = stack.getTagCompound();
            }else{
                nbt = new NBTTagCompound();
            }

            String string = getTrait(stack, nbt);

            //Add Traits
            /*
            if ((temperature <= ModConfig.coolMaxThreshold && temperature > ModConfig.icyMaxThreshold) && string.compareTo("cool") != 0) {
                removeTrait(stack, nbt);
                applyTrait(stack, nbt, "cool", Reference.COOL);
                if(ModConfig.isDebugging) {
                    System.out.println("Cool");
                }
            }
            */
        }
    }
    public void updateDryer(float temp) {
        if(ModConfig.isDebugging) {
            System.out.println("Receiving temperature from master.");
        }
        temperature = temp;
        lastUpdate = 240;
        //Syncing syncing diving diving
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
    }

    public float getTemperature() {
        return temperature;
    }
    public float getPressure() {
        return pressure;
    }
    public float getCoolant() {
        return coolant;
    }

    public float getLocalPressure() {
        return localPressure;
    }

    public float getLocalTemperature() {
        return localTemperature;
    }

    private void writeSyncData(NBTTagCompound tagCompound) {
        float temp = (lastUpdate < 0) ? -1000 : temperature;
        tagCompound.setFloat("Temperature", temp);
        tagCompound.setTag("Items", super.serializeNBT());
    }

    private void readSyncData(NBTTagCompound tagCompound) {
        temperature = tagCompound.getFloat("Temperature");
        super.deserializeNBT(tagCompound.getCompoundTag("Items"));
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound);
        writeSyncData(tagCompound);
        return new SPacketUpdateTileEntity(new BlockPos(pos.getX(), pos.getY(), pos.getZ()), 1, tagCompound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        readSyncData(packet.getNbtCompound());
    }

    @Override
    public void onBreakBlock(World world, BlockPos pos, IBlockState state)
    {
        for(int i = 0; i < 9; ++i) {
            if(ModConfig.isDebugging) {
                System.out.println("SLOT " + i);
            }
            ItemStack stack = inventory.getStackInSlot(i);
            NBTTagCompound nbt;
            if(stack.hasTagCompound()){
                nbt = stack.getTagCompound();
            }else{
                nbt = new NBTTagCompound();
            }

            stack.setTagCompound(null);
            InventoryHelper.spawnItemStack(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), stack);
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return (T) new ItemHandlerSidedWrapper(this, inventory, facing);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean canInsert(int i, ItemStack itemStack, EnumFacing enumFacing) {
        return true;
    }

    @Override
    public boolean canExtract(int i, EnumFacing enumFacing) {
        return true;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        IItemSize sizeCap = CapabilityItemSize.getIItemSize(stack);
        if (sizeCap != null)
        {
            return sizeCap.getSize(stack).isSmallerThan(Size.LARGE);
        }
        return true;
    }

    private static class FreezeDryerItemStackHandler extends ItemStackHandler implements IItemHandlerModifiable, IItemHandler, INBTSerializable<NBTTagCompound>
    {
        public FreezeDryerItemStackHandler(int size) {
            super(size);
            this.deserializeNBT(new NBTTagCompound());
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            ItemStack stack = super.extractItem(slot, amount, simulate);

            NBTTagCompound nbt;
            if(stack.hasTagCompound()){
                nbt = stack.getTagCompound();
            }else{
                nbt = new NBTTagCompound();
            }

            String string = nbt.getString("CellarAddonTemperature");
            if(string.compareTo("cool") == 0){
                CapabilityFood.removeTrait(stack, Reference.COOL);
            }
            if(string.compareTo("icy") == 0){
                CapabilityFood.removeTrait(stack, Reference.ICY);
            }
            if(string.compareTo("freezing") == 0){
                CapabilityFood.removeTrait(stack, Reference.FREEZING);
            }
            if(string.compareTo("freeze dryed") == 0){
                CapabilityFood.removeTrait(stack, Reference.DRY);
            }
            nbt.removeTag("CellarAddonTemperature");
            stack.setTagCompound(null);
            return stack;
        }

    }
}
