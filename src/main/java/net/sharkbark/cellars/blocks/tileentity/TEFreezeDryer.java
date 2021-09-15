package net.sharkbark.cellars.blocks.tileentity;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodTrait;
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
    private boolean overheating = false;
    private int tick;

    private float temperature;
    private float pressure;
    private float localPressure;
    private float coolant;
    private boolean sealed;
    private boolean pump;
    private int overheatTick;
    private int ticksSealed;

    public TEFreezeDryer() {
        super(new TEFreezeDryer.FreezeDryerItemStackHandler(10));
        localTemperature = ClimateTFC.getActualTemp(this.getPos());
        temperature = localTemperature;
        localPressure = (ModConfig.seaLevelPressure + ((-(this.getPos().getY()-ModConfig.seaLevel)) * ModConfig.pressureChange));
        pressure = localPressure;
        sealed = false;
        pump = false;
    }



    @Override
    public void update() {
        //Slow machine ticking
        if((++tick)%20 != 0){
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
            coolant = coolant - ModConfig.coolantConsumptionMultiplier * Math.abs(temperature - localTemperature);
            temperature = temperature + ModConfig.temperatureDissipation * (localTemperature - temperature) - (ModConfig.temperatureDissipation * temperature);
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
            if(sealed && pressure > ModConfig.targetPressure) {
                updatePressure(ModConfig.tickRate);
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
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, 0, 1, 0);
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, 0, 1, 0);
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, 0, 1, 0);
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, 0, 1, 0);
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, 0, 1, 0);
        if(temperature == localTemperature) {
            if((++overheatTick)%20 != 0){
                return;
            }
            overheatTick = 0;
            overheating = false;
        }
    }

    void updatePressure(int i){
        while(i<0) {
            pressure = pressure - ((ModConfig.workPerPower*getPowerLevel()*(pressure/localPressure)) / ((localPressure+1)-pressure));
            i-=1;
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
    private void removeTrait(ItemStack stack, NBTTagCompound nbt){
        String string = nbt.getString("CellarAddonTemperature");
        if(string.compareTo("preserving") == 0){
            CapabilityFood.removeTrait(stack, Reference.PRESERVING);
        }
        nbt.setString("CellarAddonTemperature","");
        stack.setTagCompound(nbt);
    }
    private void updateTraits() {
        if(ticksSealed >= ModConfig.sealedDuration) {
            for (int x = 0; x < inventory.getSlots()-1; x++) {
                ItemStack stack = inventory.getStackInSlot(x);
                NBTTagCompound nbt;
                if (stack.hasTagCompound()) {
                    nbt = stack.getTagCompound();
                } else {
                    nbt = new NBTTagCompound();
                }

                String string = getTrait(stack, nbt);

                //Add Trait
                removeTrait(stack, nbt);
                applyTrait(stack, nbt, "freezeDry", Reference.DRY);

            }
        } else {
            for (int x = 0; x < inventory.getSlots(); x++) {
                ItemStack stack = inventory.getStackInSlot(x);
                NBTTagCompound nbt;
                if (stack.hasTagCompound()) {
                    nbt = stack.getTagCompound();
                } else {
                    nbt = new NBTTagCompound();
                }

                String string = getTrait(stack, nbt);

                //Add Trait
                applyTrait(stack, nbt, "preserving", Reference.PRESERVING);
            }
        }
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
        this.markForSync();
    }

    public void unseal(){
        sealed = false;
        pump = false;
        pressure = localPressure;
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
        tagCompound.setFloat("Pressure", pressure);
        tagCompound.setFloat("LocalPressure", localPressure);
        tagCompound.setFloat("Coolant", coolant);
        tagCompound.setBoolean("Seal", sealed);
        tagCompound.setBoolean("Pump", pump);
        tagCompound.setInteger("TicksSealed", ticksSealed);
        tagCompound.setInteger("OverheatTicks", overheatTick);
    }
    private void readSyncData(NBTTagCompound tagCompound) {
        temperature = tagCompound.getFloat("Temperature");
        pressure = tagCompound.getFloat("Pressure");
        localPressure = tagCompound.getFloat("LocalPressure");
        coolant = tagCompound.getFloat("Coolant");
        sealed = tagCompound.getBoolean("Seal");
        pump = tagCompound.getBoolean("Pump");
        ticksSealed = tagCompound.getInteger("TicksSealed");
        overheatTick = tagCompound.getInteger("OverheatTicks");
    }


    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        temperature = tagCompound.getFloat("Temperature");
        pressure = tagCompound.getFloat("Pressure");
        localPressure = tagCompound.getFloat("LocalPressure");
        coolant = tagCompound.getFloat("Coolant");
        sealed = tagCompound.getBoolean("Seal");
        pump = tagCompound.getBoolean("Pump");
        ticksSealed = tagCompound.getInteger("TicksSealed");
        overheatTick = tagCompound.getInteger("OverheatTicks");
    }
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        tagCompound.setFloat("Temperature", temperature);
        tagCompound.setFloat("Pressure", pressure);
        tagCompound.setFloat("LocalPressure", localPressure);
        tagCompound.setFloat("Coolant", coolant);
        tagCompound.setBoolean("Seal", sealed);
        tagCompound.setBoolean("Pump", pump);
        tagCompound.setInteger("TicksSealed", ticksSealed);
        tagCompound.setInteger("OverheatTicks", overheatTick);

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

            NBTTagCompound nbt;
            if(stack.hasTagCompound()){
                nbt = stack.getTagCompound();
            }else{
                nbt = new NBTTagCompound();
            }

            String string = nbt.getString("CellarAddonTemperature");
            if(string.compareTo("preserving") == 0){
                CapabilityFood.removeTrait(stack, Reference.PRESERVING);
            }
            nbt.removeTag("CellarAddonTemperature");
            stack.setTagCompound(null);
            return stack;
        }

    }
}
