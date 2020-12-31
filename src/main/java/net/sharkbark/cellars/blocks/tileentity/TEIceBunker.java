package net.sharkbark.cellars.blocks.tileentity;
//ClimateTFC.getAvgTemp(this.getPos());

import com.google.common.base.Optional;
import com.mojang.authlib.properties.Property;
import net.dries007.tfc.objects.blocks.BlockSnowTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.sharkbark.cellars.ModConfig;
import net.sharkbark.cellars.blocks.BlockCellarShelf;
import net.sharkbark.cellars.blocks.CellarDoor;
import net.sharkbark.cellars.blocks.CellarWall;
import net.sharkbark.cellars.blocks.container.ContainerIceBunker;
import net.sharkbark.cellars.init.ModBlocks;
import net.sharkbark.cellars.util.Reference;

import javax.annotation.Nullable;
import java.util.Collection;

public class TEIceBunker extends TileEntityLockableLoot implements IInventory, ITickable {

    //NBT
    //private ItemStack[] inventory = null;
    private int coolantAmount = 0;
    private int lastUpdate = 0;

    private int[] entrance = new int[4];	//x, z of the first door + offsetX, offsetZ of the second door
    private int[] size = new int[4];		//internal size, +z -x -z + x
    private boolean isComplete = false;
    private boolean hasAirlock = false;

    private float avgYearTemp = Float.MIN_VALUE;
    private float lossMult = -1f;

    private float temperature = -1;	//-1000 cellar is not complete
    private byte error = 0;

    private int updateTickCounter = 1200;
    private NonNullList<ItemStack> chestContents = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);


    public TEIceBunker(){
    }

    public void getCellarInfo(EntityPlayer player) {
        if(ModConfig.isDebugging) {
            player.sendMessage(new TextComponentString("Temperature: " + temperature + " Coolant: " + coolantAmount));
            player.sendMessage(new TextComponentString("Check console for more information"));
            player.sendMessage(new TextComponentString("The current error number is: " + error));
            player.sendMessage(new TextComponentString("Is the cellar complete: " + isComplete));
            System.out.println("HELLO WORLD");
            //updateCellar(true);
            return;
        }

        if(isComplete) {
            if(temperature < 0) {
                player.sendMessage(new TextComponentString("It is icy here"));
            } else if(temperature < 5) {
                player.sendMessage(new TextComponentString("It is freezing here"));
            } else {
                player.sendMessage(new TextComponentString("The cellar is chilly"));
            }
        } else {
            player.sendMessage(new TextComponentString("The cellar is not complete or is not chilled yet"));
        }
    }



    @Override
    public void update() {
        if(world.isRemote) {
            return;
        }

        //Check cellar compliance once per 1200 ticks, check coolant and update containers once per 100 ticks
        if(updateTickCounter % 100 == 0) {

            if(updateTickCounter >= 1200) {
                updateCellar(true);
                updateTickCounter = 0;
            } else {
                updateCellar(false);
            }

            updateContainers();
        }
        updateTickCounter++;
    }

    private void updateCellar(boolean checkCompliance) {
        if (avgYearTemp == Float.MIN_VALUE) {
            for (int month = 0; month < 12; month++) {
                avgYearTemp += ClimateTFC.getActualTemp(this.getPos());
            }
            avgYearTemp = avgYearTemp * 0.015f;    //Magic! (divide by 12 (average) * 0.18)
        }
        temperature = avgYearTemp;

        if(checkCompliance) {
            this.isComplete = isStructureComplete();
        }

        if(isComplete) {
            float outsideTemp = ClimateTFC.getActualTemp(this.getPos());
            if(coolantAmount <= 0) {
                for(int slot = 3; slot >= 0; slot--) {
                    if(!chestContents.get(slot).isEmpty()) {
                        if(Block.getBlockFromItem(chestContents.get(slot).getItem()) instanceof BlockIce) {
                            coolantAmount = coolantAmount + 120;
                        } else if(Block.getBlockFromItem(chestContents.get(slot).getItem()) instanceof BlockSnow) {
                            coolantAmount = coolantAmount + 40;
                        }
                        lastUpdate = (int)CalendarTFC.CALENDAR_TIME.getTotalDays();
                        decrStackSize(slot, 1);
                        temperature = ModConfig.iceHouseTemperature;
                        break;
                    }
                }
            }

            if(coolantAmount > 0) {
                temperature = ModConfig.iceHouseTemperature;
                if(lastUpdate < (int)CalendarTFC.CALENDAR_TIME.getTotalDays()) {
                    if(outsideTemp > -10) {	//magic
                        int volume = (size[1] + size[3] + 1) * (size[0] + size[2] + 1);
                        coolantAmount = coolantAmount - (int)(ModConfig.coolantConsumptionMultiplier * (0.05 * volume * (1 + lossMult) * (outsideTemp + volume + 2)));
                    }
                    lastUpdate++;
                }
            }

            float doorsLossMult = doorsLossMult();
            if(lossMult == -1) {
                lossMult = doorsLossMult;
            }

            if(lossMult != doorsLossMult) {
                if(lossMult > doorsLossMult) {
                    lossMult = (lossMult - 0.01f) * 0.75f;
                    lossMult = Math.max(doorsLossMult, lossMult);
                } else {
                    lossMult = (lossMult + 0.01f) * 1.15f;	//0.05f because lossMult can to be 0
                    lossMult = Math.min(doorsLossMult, lossMult);
                }
            }

            temperature = temperature + lossMult * outsideTemp;
            if(temperature > outsideTemp) {
                temperature = outsideTemp;
            }
        }

        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos),2);
    }

    private float doorsLossMult() {
        float loss = 0;

        int posX = pos.getX() + entrance[0];
        int posY = pos.getY() + 1;
        int posZ = pos.getZ() + entrance[1];


        //1st door
        Block door = world.getBlockState(new BlockPos(posX, posY, posZ)).getBlock();
        if(door == ModBlocks.CELLAR_DOOR && BlockDoor.isOpen(world, new BlockPos(posX, posY, posZ))) {

            loss = 0.05f;
        }

        //2nd door
        //Does it even exist?
        if(!hasAirlock) {
            return loss * 8 + 0.3f;
        }

        door = world.getBlockState(new BlockPos(posX + entrance[2], posY, posZ + entrance[3])).getBlock();
        if(door == ModBlocks.CELLAR_DOOR && BlockDoor.isOpen( world, new BlockPos(posX + entrance[2], posY, posZ + entrance[3]))) {

            return loss * 13 + 0.05f;
        }

        return loss;
    }

    private boolean isStructureComplete() {
        entrance[0] = 0; entrance[1] = 0;
        entrance[2] = 0; entrance[3] = 0;

        hasAirlock = false;
        error = 0;

        int blockType = -1;

        //get size
        for(int direction = 0; direction < 4; direction++) {
            for(int distance = 1; distance < 6; distance++) {
                //max distance between an ice bunker and a wall is 3
                if(distance == 5) {
                    if(ModConfig.isDebugging) {
                        System.out.println("Cellar at " + pos.getX() + " " + pos.getY() + " " + pos.getZ()
                                + " can't find a wall on " + direction + " side");
                    }
                    error = 1;
                    return false;
                }

                if(direction == 1) {			blockType = getBlockType(-distance, 1, 0); }
                else if(direction == 3) {	blockType = getBlockType(distance, 1, 0); }
                else if(direction == 2) {	blockType = getBlockType(0, 1, -distance); }
                else if(direction == 0) {	blockType = getBlockType(0, 1, distance); }

                if(blockType == 0 || blockType == 1) {
                    size[direction] = distance - 1;
                    break;
                }

                if(blockType == -1) {
                    error = 2;
                    return false;
                }
            }
        }

        //check blocks and set entrance
        for(int y = 0; y < 4; y++) {
            for(int x = -size[1] - 1; x <= size[3] + 1; x++) {
                for(int z = -size[2] - 1; z <= size[0] + 1; z++) {

                    //Ice bunker
                    if(y == 0 && x == 0 && z == 0) {
                        continue;
                    }

                    blockType = getBlockType(x, y, z);

                    //Blocks inside the cellar
                    if(y == 1 || y == 2) {
                        if(x >= -size[1] && x <= size[3]) {
                            if(z >= -size[2] && z <= size[0]) {
                                if(blockType == 2) {
                                    continue;
                                }
                                error = 2;
                                return false;
                            }
                        }
                    }

                    //Corners
                    if((x == -size[1] - 1 || x == size[3] + 1) && (z == -size[2] - 1 || z == size[0] + 1)) {
                        if(blockType == 0) {
                            continue;
                        }
                        error = 1;
                        return false;
                    }

                    //Doors
                    if(blockType == 1) {
                        //upper part of the door
                        if(entrance[0] == x && entrance[1] == z) {
                            continue;
                        }

                        //1 entrance only!
                        if(entrance[0] == 0 && entrance[1] == 0) {
                            entrance[0] = x; entrance[1] = z;
                            if(x == -size[1] - 1) {
                                entrance[2] = -1;
                            } else if(x == size[3] + 1) {
                                entrance[2] = 1;
                            } else if(z == -size[2] - 1) {
                                entrance[3] = -1;
                            } else if(z == size[0] + 1) {
                                entrance[3] = 1;
                            }

                            continue;
                        }

                        if(ModConfig.isDebugging) {
                            System.out.println("Cellar at " + pos.getX() + " " + pos.getY() + " " + pos.getZ()
                                    + " has too many doors");
                        }
                        error = 3;
                        return false;
                    }

                    //Walls
                    if(blockType == 0) {
                        continue;
                    }
                    error = 1;
                    return false;
                }
            }
        }

        if(entrance[0] == 0 && entrance[1] == 0) {
            if(ModConfig.isDebugging) {
                System.out.println("Cellar at " + pos.getX() + " " + pos.getY() + " " + pos.getZ()
                        + " has no doors");
            }
            error = 3;
            return false;
        }

        //check the entrance
        for(int y = 0; y < 4; y++) {
            for(int x = -MathHelper.abs(entrance[3]); x <= MathHelper.abs(entrance[3]); x++) {
                for(int z = -MathHelper.abs(entrance[2]); z <= MathHelper.abs(entrance[2]); z++ ) {

                    blockType = getBlockType(x + entrance[0] + entrance[2], y, z + entrance[1] + entrance[3]);

                    if(y == 1 || y == 2) {
                        if(x == 0 && z == 0) {
                            if(blockType == 1) {
                                hasAirlock = true;
                                continue;
                            }

                            hasAirlock = false;
                            if(ModConfig.isDebugging) {
                                System.out.println("Cellar at " + pos.getX() + " " + pos.getY() + " " + pos.getZ()
                                        + " doesn't has the second door, block there is " + blockType);
                            }
                        }
                    }
                    if(blockType == 0) {
                        continue;
                    }

                    hasAirlock = false;
                    if(ModConfig.isDebugging) {
                        System.out.println("Door at " + pos.getX() + " " + pos.getY() + " " + pos.getZ()
                                + " doesn't surrounded by wall, block there is " + blockType);
                    }
                }
            }
        }

        if(ModConfig.isDebugging) {
            System.out.println("Cellar at " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " is complete");
        }

        return true;
    }

    private int getBlockType(int x, int y, int z) {
        Block block = world.getBlockState(new BlockPos(getPos().getX() + x, getPos().getY() + y, getPos().getZ() + z)).getBlock();
        if(block instanceof CellarWall) {
            return 0;
        } else if(block instanceof CellarDoor) {
            return 1;
        } else if(block instanceof BlockCellarShelf || block instanceof BlockWallSign || block instanceof BlockStandingSign ||
                world.isAirBlock(new BlockPos(getPos().getX() + x, getPos().getY() + y, getPos().getZ() + z))) {
            return 2;
        }

        if(ModConfig.isDebugging) {
            System.out.println("Incorrect cellar block at " + x + " " + y + " " + z + " " + block.getRegistryName());
        }

        return -1;
    }

    public void updateContainers() {
        for(int y = 1; y <=2; y++) {
            for(int z = -size[2]; z <= size[0]; z++) {
                for(int x = -size[1]; x <= size[3]; x++) {
                    updateContainer(x, y, z);
                }
            }
        }
    }

    private void updateContainer(int x, int y, int z) {
        Block block = world.getBlockState(new BlockPos(getPos().getX() + x, getPos().getY() + y, getPos().getZ() + z)).getBlock();
        if(block instanceof BlockCellarShelf) {
            TileEntity tileEntity = world.getTileEntity(new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z));
            if(tileEntity != null) {
                ((TECellarShelf) tileEntity).updateShelf(temperature);
            }

            return;
        }
    }

    public float getTemperature() {
        return temperature;
    }

    @Override
    public int getSizeInventory() {
        return 4;
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack stack : this.chestContents){
            if(stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer entityPlayer) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    private void writeSyncData(NBTTagCompound tagCompound) {
        float temp = (error == 0) ? temperature : (-1 * error * 1000);
        tagCompound.setFloat("Temperature", temp);
    }

    private void readSyncData(NBTTagCompound tagCompound) {
        temperature = tagCompound.getFloat("Temperature");
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        this.chestContents = NonNullList.<ItemStack>withSize(getSizeInventory(), ItemStack.EMPTY);

        if(!this.checkLootAndRead(tagCompound)) ItemStackHelper.loadAllItems(tagCompound, chestContents);
        if(tagCompound.hasKey("CustomName", 8)) this.customName = tagCompound.getString("CustomName");

        lastUpdate = tagCompound.getInteger("LastUpdate");
        coolantAmount = tagCompound.getInteger("CoolantAmount");
        error = tagCompound.getByte("ErrorCode");
        isComplete = tagCompound.getBoolean("isCompliant");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);

        if(!this.checkLootAndRead(tagCompound)) ItemStackHelper.saveAllItems(tagCompound, chestContents);
        if(tagCompound.hasKey("CustomName", 8)) tagCompound.setString("CustomName", this.customName);

        tagCompound.setInteger("LastUpdate", lastUpdate);
        tagCompound.setInteger("CoolantAmount", coolantAmount);
        tagCompound.setByte("ErrorCode", error);
        tagCompound.setBoolean("isCompliant", isComplete);

        return tagCompound;
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        readFromNBT(packet.getNbtCompound());
        readSyncData(packet.getNbtCompound());
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
    public String getName() {
        return this.hasCustomName() ? this.customName : "container.ice_bunker";
    }

    @Override
    public Container createContainer(InventoryPlayer inventoryPlayer, EntityPlayer entityPlayer) {
        return new ContainerIceBunker(inventoryPlayer, this, entityPlayer);
    }

    @Override
    public String getGuiID() {
        return Reference.MOD_ID+"ice_bunker";
    }

    @Override
    public int getInventoryStackLimit(){
        return 64;
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    @Override
    public void openInventory(EntityPlayer entityPlayer) {
        this.world.notifyNeighborsOfStateChange(pos, this.getBlockType(), false);
    }

    @Override
    public void closeInventory(EntityPlayer p_closeInventory_1_) {
        this.world.notifyNeighborsOfStateChange(pos, this.getBlockType(), false);
    }
}
