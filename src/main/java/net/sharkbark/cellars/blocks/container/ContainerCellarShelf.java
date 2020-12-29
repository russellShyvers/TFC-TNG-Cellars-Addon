package net.sharkbark.cellars.blocks.container;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.objects.container.ContainerTE;
import net.dries007.tfc.objects.inventory.slot.SlotCallback;
import net.dries007.tfc.util.agriculture.Food;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.sharkbark.cellars.blocks.tileentity.TECellarShelf;
import net.sharkbark.cellars.foods.CellarTrait;

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
