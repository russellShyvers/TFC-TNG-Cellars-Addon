package net.sharkbark.cellars.blocks.container;

import net.dries007.tfc.objects.container.ContainerTE;
import net.dries007.tfc.objects.inventory.slot.SlotCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.sharkbark.cellars.ModConfig;
import net.sharkbark.cellars.blocks.tileentity.TEFreezeDryer;

public class ContainerFreezeDryer extends ContainerTE<TEFreezeDryer> {
    public ContainerFreezeDryer(InventoryPlayer playerInv, TEFreezeDryer tile, EntityPlayer player) {
        super(playerInv, tile);
    }

    @Override
    protected void addContainerSlots() {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        if (inventory != null)
        {
            for (int y = 0; y < 3; y++)
            {
                for (int x = 0; x < 3; x++)
                {
                    if(ModConfig.isDebugging) {
                        System.out.println("Adding slot x: "+ x +" y: " + y);
                    }
                    addSlotToContainer(new SlotCallback(inventory, x+y*3, x*18+17, y*18+17, tile));
                }
            }
        }
    }
}
