package net.sharkbark.cellars.blocks.tileentity;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.inventory.capability.IItemHandlerSidedCallback;
import net.dries007.tfc.objects.inventory.capability.ItemHandlerSidedWrapper;
import net.dries007.tfc.objects.te.TEInventory;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
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
import net.sharkbark.cellars.init.ModItems;
import net.sharkbark.cellars.util.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.sharkbark.cellars.blocks.FreezeDryer.FACING;

public class TEFreezeDryer extends TEInventory implements IItemHandlerSidedCallback, ITickable {

    private float localTemperature;
    public boolean overheating = false;
    private int tick;

    private float temperature;
    private double pressure;
    private double localPressure;
    private float coolant;
    private boolean sealed;
    private boolean pump;
    public int overheatTick;
    private int ticksSealed;
    public boolean initialized;

    public TEFreezeDryer() {
        super(new TEFreezeDryer.FreezeDryerItemStackHandler(10));
        initialized = false;
    }



    @Override
    public void update() {
        if(!initialized){
            initialized=true;
            localTemperature = ClimateTFC.getActualTemp(this.getPos());
            temperature = localTemperature;
            localPressure = (ModConfig.seaLevelPressure + ((-(this.getPos().getY()-ModConfig.seaLevel)) * ModConfig.pressureChange));
            System.out.println("Local pos: "+ this.getPos());
            System.out.println("Local pressure is: "+ localPressure);
            pressure = localPressure;
            sealed = false;
            pump = false;
            ticksSealed = 0;
        }

        //Slow machine ticking
        if((++tick)%20==0){
            return;
        }

        //Reset tick count
        tick = 0;

        //Get current local temperature at block pos
        localTemperature = ClimateTFC.getActualTemp(this.getPos());

        //Consume a piece of coolant
        handleCoolant();

        //Dissipate Heat
        if(coolant > ModConfig.coolantConsumptionMultiplier * Math.abs(temperature - localTemperature) && pump) {
            temperature = temperature + ModConfig.temperatureDissipation * (localTemperature - temperature);

            //Only consume coolant if needed.
            if(temperature >= ModConfig.maxTemp){
                coolant = coolant - ModConfig.coolantConsumptionMultiplier * Math.abs(temperature - localTemperature);
                temperature = temperature - (ModConfig.temperatureDissipation * temperature);
            }
        } else {
            temperature = temperature + ModConfig.temperatureDissipation * (localTemperature - temperature);
        }

        //Disabled till it cools back down
        if(overheating){
            overheatTick();
        }

        //Handle pumping action
        if(world.isBlockPowered(this.getPos()) && !overheating && pump) {

            //Increase heat
            temperature = temperature + (ModConfig.heatPerPower * getPowerLevel());

            //Decrease pressure
            if(sealed) {
                pressure = pressure - (getPowerLevel()*ModConfig.workPerPower*ModConfig.tickRate*pressure)/Math.pow(localPressure,2);
            }

            if(pressure < ModConfig.targetPressure){
                pressure = ModConfig.targetPressure;
            }

            spawnParticles();
        }

        if(temperature >= ModConfig.maxTemp){
            overheating = true;
        }

        if (sealed && pressure <= ModConfig.targetPressure){
            if(ticksSealed < ModConfig.sealedDuration){
                ticksSealed+=1;
            }
        }

        if (sealed) {
            updateTraits();
        }

        this.markForSync();
    }

    private void handleCoolant() {
        if (!inventory.getStackInSlot(9).isEmpty()) {
            Item item = inventory.getStackInSlot(9).getItem();
            if ((item == ModItems.PACKED_ICE_SHARD || Block.getBlockFromItem(item) == Blocks.PACKED_ICE) && coolant < ModConfig.coolantMax - ModConfig.packedIceCoolant) {
                coolant = coolant + ModConfig.packedIceCoolant;
                inventory.extractItem(9, 1, false);
            } else if ((item == ModItems.SEA_ICE_SHARD || Block.getBlockFromItem(item) == BlocksTFC.SEA_ICE) && coolant < ModConfig.coolantMax - ModConfig.seaIceCoolant) {
                coolant = coolant + ModConfig.seaIceCoolant;
                inventory.extractItem(9, 1, false);
            } else if ((item == ModItems.ICE_SHARD || Block.getBlockFromItem(item) == Blocks.ICE) && coolant < ModConfig.coolantMax - ModConfig.iceCoolant) {
                coolant = coolant + ModConfig.iceCoolant;
                inventory.extractItem(9, 1, false);
            } else if ((Block.getBlockFromItem(item) == Blocks.SNOW) && coolant < ModConfig.coolantMax - ModConfig.snowCoolant) {
                coolant = coolant + ModConfig.snowCoolant;
                inventory.extractItem(9, 1, false);
            }else if ((item == Items.SNOWBALL) && coolant < ModConfig.coolantMax - ModConfig.snowBallCoolant) {
                coolant = coolant + ModConfig.snowBallCoolant;
                inventory.extractItem(9, 1, false);
            }
        }
    }

    private void overheatTick() {
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, 0, Math.random(), 0);
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, 0, Math.random(), 0);
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, 0, Math.random(), 0);
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, 0, Math.random(), 0);
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, 0, Math.random(), 0);
        if(temperature <= localTemperature) {
            if((++overheatTick)%100 != 0){
                return;
            }
            overheatTick = 0;
            overheating = false;
        }
    }

    public int getPowerLevel() {
        EnumFacing facing = world.getBlockState(this.getPos()).getValue(FACING);
        if(world.isBlockPowered(this.getPos())){
            if(EnumFacing.NORTH == facing) {
                return world.getRedstonePower(getPos().offset(EnumFacing.SOUTH), EnumFacing.SOUTH);
            } else if(EnumFacing.EAST == facing) {
                return world.getRedstonePower(getPos().offset(EnumFacing.WEST), EnumFacing.WEST);
            } else if(EnumFacing.SOUTH == facing) {
                return world.getRedstonePower(getPos().offset(EnumFacing.NORTH), EnumFacing.NORTH);
            } else if(EnumFacing.WEST == facing) {
                return world.getRedstonePower(getPos().offset(EnumFacing.EAST), EnumFacing.EAST);
            }
        } else {
            return 0;
        }
        return 0;
    }

    private void spawnParticles() {
        EnumFacing facing = world.getBlockState(this.getPos()).getValue(FACING);
        if (world.isRemote) {
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

    private void applyTrait(ItemStack stack, FoodTrait trait){
        IFood food = (IFood)stack.getCapability(CapabilityFood.CAPABILITY, (EnumFacing)null);
        if (!stack.isEmpty() && food != null) {
            if(trait == Reference.PRESERVING && (food.getTraits().contains(Reference.DRY))){
                return;
            }
        }
        CapabilityFood.applyTrait(stack, trait);
    }
    private void removeTrait(ItemStack stack, FoodTrait trait){
        CapabilityFood.removeTrait(stack, trait);
    }

    private void applyTraits() {
        for (int x = 0; x < inventory.getSlots()-1; x++) {
                ItemStack stack = inventory.getStackInSlot(x);
                applyTrait(stack, Reference.PRESERVING);
        }
    }
    private void removeTraits() {
        for (int x = 0; x < inventory.getSlots()-1; x++) {
                ItemStack stack = inventory.getStackInSlot(x);
                removeTrait(stack, Reference.PRESERVING);
        }
    }

    private void updateTraits() {
        if(ticksSealed >= ModConfig.sealedDuration) {
            for (int x = 0; x < inventory.getSlots()-1; x++) {
                ItemStack stack = inventory.getStackInSlot(x);

                removeTrait(stack, Reference.PRESERVING);
                applyTrait(stack, Reference.DRY);
            }
        }
    }

    public double getTemperature() {
        return temperature;
    }
    public double getPressure() {
        return pressure;
    }
    public double getCoolant() {
        return coolant;
    }
    public double getLocalPressure() {
        return localPressure;
    }
    public double getLocalTemperature() {
        return localTemperature;
    }
    public boolean getSeal() {
        return sealed;
    }
    public int getPower() {
        return getPowerLevel();
    }
    public Boolean getPump() {
        return pump;
    }

    public void seal(){
        sealed = true;
        applyTraits();
        this.markForSync();
    }

    public void unseal(){
        sealed = false;
        pump = false;
        ticksSealed = 0;
        pressure = localPressure;
        removeTraits();
        this.markForSync();
    }

    public void startPump(){
        pump = true;
        this.markForSync();
    }

    public void stopPump(){
        pump = false;
        this.markForSync();
    }

    private void writeSyncData(NBTTagCompound tagCompound) {
        tagCompound.setFloat("Temperature", temperature);
        tagCompound.setDouble("Pressure", pressure);
        tagCompound.setDouble("LocalPressure", localPressure);
        tagCompound.setFloat("Coolant", coolant);
        tagCompound.setBoolean("Seal", sealed);
        tagCompound.setBoolean("Pump", pump);
        tagCompound.setInteger("TicksSealed", ticksSealed);
        tagCompound.setInteger("OverheatTicks", overheatTick);
        tagCompound.setBoolean("Overheating", overheating);
        tagCompound.setBoolean("Initialized", initialized);
    }
    private void readSyncData(NBTTagCompound tagCompound) {
        temperature = tagCompound.getFloat("Temperature");
        pressure = tagCompound.getDouble("Pressure");
        localPressure = tagCompound.getDouble("LocalPressure");
        coolant = tagCompound.getFloat("Coolant");
        sealed = tagCompound.getBoolean("Seal");
        pump = tagCompound.getBoolean("Pump");
        ticksSealed = tagCompound.getInteger("TicksSealed");
        overheatTick = tagCompound.getInteger("OverheatTicks");
        overheating = tagCompound.getBoolean("Overheating");
        initialized = tagCompound.getBoolean("Initialized");
    }


    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        temperature = tagCompound.getFloat("Temperature");
        pressure = tagCompound.getDouble("Pressure");
        localPressure = tagCompound.getDouble("LocalPressure");
        coolant = tagCompound.getFloat("Coolant");
        sealed = tagCompound.getBoolean("Seal");
        pump = tagCompound.getBoolean("Pump");
        ticksSealed = tagCompound.getInteger("TicksSealed");
        overheatTick = tagCompound.getInteger("OverheatTicks");
        overheating = tagCompound.getBoolean("Overheating");
        initialized = tagCompound.getBoolean("Initialized");
    }
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("Temperature", temperature);
        tagCompound.setDouble("Pressure", pressure);
        tagCompound.setDouble("LocalPressure", localPressure);
        tagCompound.setFloat("Coolant", coolant);
        tagCompound.setBoolean("Seal", sealed);
        tagCompound.setBoolean("Pump", pump);
        tagCompound.setInteger("TicksSealed", ticksSealed);
        tagCompound.setInteger("OverheatTicks", overheatTick);
        tagCompound.setBoolean("Overheating", overheating);
        tagCompound.setBoolean("Initialized", initialized);

        return tagCompound;
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
        for(int i = 0; i < 10; ++i) {
            ItemStack stack = inventory.getStackInSlot(i);

            removeTraits();

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

        if (sealed && i < 9){
            return false;
        }

        if ((itemStack.getItem() == ModItems.SEA_ICE_SHARD ||
            itemStack.getItem() == ModItems.PACKED_ICE_SHARD ||
            itemStack.getItem() == ModItems.ICE_SHARD ||
            itemStack.getItem() == Items.SNOWBALL ||
            itemStack.getItem() == Item.getItemFromBlock(Blocks.ICE) ||
            itemStack.getItem() == Item.getItemFromBlock(Blocks.PACKED_ICE) ||
            itemStack.getItem() == Item.getItemFromBlock(BlocksTFC.SEA_ICE) ||
            itemStack.getItem() == Item.getItemFromBlock(Blocks.SNOW)) && i != 9){

            return false;
        }

        return true;
    }

    @Override
    public boolean canExtract(int i, EnumFacing enumFacing) {
        if (sealed && i < 9){
            return false;
        }
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

    public int getSealedFor() {
        return ticksSealed;
    }

    public float getSealedTicks() {
        return ticksSealed;
    }


    private static class FreezeDryerItemStackHandler extends ItemStackHandler implements IItemHandlerModifiable, IItemHandler, INBTSerializable<NBTTagCompound>
    {
        public FreezeDryerItemStackHandler(int size) {
            super(size);
            this.deserializeNBT(new NBTTagCompound());
        }

        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return super.insertItem(slot, stack, simulate);
        }

        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            ItemStack stack = super.extractItem(slot, amount, simulate);
            CapabilityFood.removeTrait(stack, Reference.PRESERVING);
            return stack;
        }

    }
}
