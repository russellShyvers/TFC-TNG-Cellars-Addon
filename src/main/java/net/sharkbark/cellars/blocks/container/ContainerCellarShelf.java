package net.sharkbark.cellars.blocks.container;

import net.dries007.tfc.objects.container.ContainerTE;
import net.dries007.tfc.objects.inventory.slot.SlotCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.sharkbark.cellars.blocks.tileentity.TECellarShelf;

public class ContainerCellarShelf extends ContainerTE<TECellarShelf> {
    public ContainerCellarShelf(InventoryPlayer playerInv, TECellarShelf tile, EntityPlayer player) {
        super(playerInv, tile);
    }

    @Override
    protected void addContainerSlots() {
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        if (inventory != null)
        {
            for (int y = 0; y < 2; y++)
            {
                for (int x = 0; x < 7; x++)
                {

                    addSlotToContainer(new SlotCallback(inventory, x+y*7, 8+x*18+18, 18+y*18+7, tile));
                }
            }
        }
    }
}
