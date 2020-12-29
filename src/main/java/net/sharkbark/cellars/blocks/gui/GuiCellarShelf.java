package net.sharkbark.cellars.blocks.gui;

import net.dries007.tfc.client.button.IButtonTooltip;
import net.dries007.tfc.client.gui.GuiContainerTE;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.sharkbark.cellars.blocks.container.ContainerCellarShelf;
import net.sharkbark.cellars.blocks.tileentity.TECellarShelf;
import net.sharkbark.cellars.util.Reference;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.button.GuiButtonLargeVesselSeal;
import net.dries007.tfc.client.button.IButtonTooltip;
import net.dries007.tfc.network.PacketGuiButton;

public class GuiCellarShelf extends GuiContainerTE<TECellarShelf> {

    public static final ResourceLocation CELLAR_SHELF_BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/cellar_shelf.png");
    private final String translationKey;
    private static TECellarShelf TE;
    private final InventoryPlayer playerInventory;

    public GuiCellarShelf(Container container, InventoryPlayer playerInv, TECellarShelf tile, String translationKey)
    {
        super(container, playerInv, tile, CELLAR_SHELF_BACKGROUND);
        this.playerInventory = playerInv;
        this.TE = tile;
        this.translationKey = translationKey;
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    public void onGuiClosed() {
        TE.isOpen -= 1;
        super.onGuiClosed();
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String name = I18n.format(translationKey + ".name");
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 00000000);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize-92, 00000000);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        if(mouseX >= guiLeft + 5 && mouseX <= guiLeft + 15 && mouseY >= guiTop + 5 && mouseY <= guiTop + 15) {
            List<String> infoText = new ArrayList<String>();
            float temperature = TE.getTemperature();

            if(temperature == -1000) {
                infoText.add("[!] The shelf is not inside a cellar");
            } else {
                if(temperature < 0) {
                    infoText.add("Temperature: below zero");
                } else {
                    infoText.add("Temperature: " + String.format("%.2f", temperature));
                }
            }

            this.drawHoveringText(infoText, mouseX, mouseY, this.fontRenderer);
        }
    }

}
