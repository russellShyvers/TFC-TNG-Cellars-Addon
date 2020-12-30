package net.sharkbark.cellars.blocks.tileentity;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.objects.inventory.capability.IItemHandlerSidedCallback;
import net.dries007.tfc.objects.inventory.capability.ItemHandlerSidedWrapper;
import net.dries007.tfc.objects.te.TEInventory;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;

import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.sharkbark.cellars.util.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TECellarShelf extends TEInventory implements IItemHandlerSidedCallback, ITickable {
    //private NonNullList<ItemStack> chestContents = NonNullList.<ItemStack>withSize(14, ItemStack.EMPTY);
    private int cellarTick = -240;    //Because a bunker may be not in the same chunk
    public float temperature = -1;
    //public int isOpen = 0;
    private int updateTickCounter = 120;


    public TECellarShelf() {
        super(new CellarShelfItemStackHandler(14));
    }

    @Override
    public void update() {
        if(world.isRemote) {
            return;
        }

        if(updateTickCounter % 5 == 0) {
            //if(isOpen == 0) {
            handleItemTicking();
            //}
        }

        updateTickCounter++;
    }

    public static float decay(float temp) {
        if (temp > 0) {
            float tempFactor = 1f - (15f / (15f + temp));
            return tempFactor * 2;
        } else
            return 0;

    }

    private void handleItemTicking() {
        float envDecay = 1f;

        if (cellarTick >= 0) {
            if (cellarTick > 0) {
                envDecay = decay(temperature);
                cellarTick--;

                //Syncing
                if (cellarTick == 0) {
                    world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
                }
            }
            //Updates shelf contents.
            updateTraits();
        } else {
            cellarTick++;

            //Syncing syncing
            if (cellarTick == 0) {
                world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
            }
        }
    }

    private String getTrait(ItemStack stack, NBTTagCompound nbt){
        String string = nbt.getString("CellarAddonTemperature");
        if(string == "cool"){
            return "cool";
        }else if(string == "icy"){
            return "icy";
        }else if(string == "freezing"){
            return "freezing";
        }
        return "";
    }

    private void removeTrait(ItemStack stack){
        NBTTagCompound nbt;
        if(stack.hasTagCompound()){
            nbt = stack.getTagCompound();
        }else{
            nbt = new NBTTagCompound();
        }

        String string = nbt.getString("CellarAddonTemperature");
        if(string == "cool"){
            CapabilityFood.removeTrait(stack, Reference.COOL);
        }else if(string == "icy"){
            CapabilityFood.removeTrait(stack, Reference.ICY);
        }else if(string == "freezing"){
            CapabilityFood.removeTrait(stack, Reference.FREEZING);
        }
        stack.setTagCompound(nbt);
    }

    private void applyTrait(ItemStack stack, NBTTagCompound nbt, String string, FoodTrait trait){
        nbt.setString("CellarAddonTemperature", "cool");
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

            if (temperature > 20 || temperature <= -1000) {
                removeTrait(stack);
            } else if (temperature <= 0 && string != "freezing") {
                removeTrait(stack);
                applyTrait(stack, nbt, string, Reference.FREEZING);
            } else if (temperature <= 5 && string != "icy") {
                removeTrait(stack);
                applyTrait(stack, nbt, string, Reference.ICY);
            } else if (temperature <= 20 && string != "cool" ) {
                removeTrait(stack);
                applyTrait(stack, nbt, string, Reference.COOL);
            }
        }
    }

    public void updateShelf(float temp) {
        cellarTick = 100;
        temperature = temp;
        //Syncing syncing diving diving
        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 2);
    }

    public float getTemperature() {
        return temperature;
    }

    private void writeSyncData(NBTTagCompound tagCompound) {
        float temp = (cellarTick <= 0) ? -1000 : temperature;
        tagCompound.setFloat("Temperature", temp);
    }

    private void readSyncData(NBTTagCompound tagCompound) {
        temperature = tagCompound.getFloat("Temperature");
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
        System.out.println("BREAKING BLOCK !!!! DROPPING");
        for(int i = 0; i < 14; ++i) {
            System.out.println("SLOT " + i);
            ItemStack stack = inventory.getStackInSlot(i);
            removeTrait(stack);
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



    private static class CellarShelfItemStackHandler extends ItemStackHandler implements IItemHandler
    {
        public CellarShelfItemStackHandler(int size){
            super(size);
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
            if(string == "cool"){
                nbt.removeTag("cool");
                CapabilityFood.removeTrait(stack, Reference.COOL);
            }else if(string == "icy"){
                nbt.removeTag("icy");
                CapabilityFood.removeTrait(stack, Reference.ICY);
            }else if(string == "freezing"){
                nbt.removeTag("freezing");
                CapabilityFood.removeTrait(stack, Reference.FREEZING);
            }

            stack.setTagCompound(nbt);
            return stack;
        }

    }
}

