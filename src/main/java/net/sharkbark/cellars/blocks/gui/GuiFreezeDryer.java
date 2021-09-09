package net.sharkbark.cellars.blocks.gui;

import net.dries007.tfc.client.gui.GuiContainerTE;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.sharkbark.cellars.ModConfig;
import net.sharkbark.cellars.blocks.tileentity.TEFreezeDryer;
import net.sharkbark.cellars.util.Reference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiFreezeDryer extends GuiContainerTE<TEFreezeDryer> {

    public static final ResourceLocation FREEZE_DRYER_BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/freeze_dryer.png");
    private final String translationKey;
    private static TEFreezeDryer TE;
    private final InventoryPlayer playerInventory;
    private String clicked = "False";

    public GuiFreezeDryer(Container container, InventoryPlayer playerInv, TEFreezeDryer tile, String translationKey)
    {
        super(container, playerInv, tile, FREEZE_DRYER_BACKGROUND);
        this.playerInventory = playerInv;
        this.TE = tile;
        this.translationKey = translationKey;
    }


    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseX >= guiLeft + 5 && mouseX <= guiLeft + 15 && mouseY >= guiTop + 5 && mouseY <= guiTop + 15){
            clicked = "True";
        }
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String name = I18n.format(translationKey + ".name");
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 00000000);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize-92, 00000000);

        if(mouseX >= guiLeft + 5 && mouseX <= guiLeft + 15 && mouseY >= guiTop + 5 && mouseY <= guiTop + 15) {
            List<String> infoText = new ArrayList<String>();
            int temperature = (int)TE.getTemperature();
            int pressure = (int)TE.getPressure();
            int lpressure = (int)TE.getLocalPressure();
            /*
            if(temperature <= -1000) {
                infoText.add("[!] The shelf is not inside a cellar");
            } else {
                if(temperature < 0) {
                    infoText.add("Temperature: below zero");
                } else {
                    infoText.add("Temperature: " + String.format("%.2f", temperature));
                }
            }
            */
            infoText.add("Temperature: " + temperature);
            infoText.add("Pressure: " + pressure);
            infoText.add("Clicked: " + clicked);

            this.drawHoveringText(infoText, this.xSize-175, this.ySize-150, this.fontRenderer);
        }



    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0f,1.0f,1.0f,1.0f);
        this.mc.getTextureManager().bindTexture(FREEZE_DRYER_BACKGROUND);
        this.drawTexturedModalRect(this.guiLeft,this.guiTop, 0, 0, this.xSize, this.ySize);

        if(true){
            int k = (int)this.getPressureLeftScaled(51);
            this.drawTexturedModalRect(this.guiLeft+74, this.guiTop+17+52-k, 180, 52-k-1, 5, k+1 );
        }

        if(true){
            int k = (int)this.getHeatLeftScaled(51);
            this.drawTexturedModalRect(this.guiLeft+82, this.guiTop+17+52-k, 188, 52-k-1, 5, k+1 );
        }

        if(true){
            int k = (int)this.getCoolentLeftScaled(51);
            this.drawTexturedModalRect(this.guiLeft+163, this.guiTop+17+52-k, 196, 52-k-1, 5, k+1 );
        }

        if(true){
            int k = (int)this.getLocalPressureScaled(51);
            this.drawTexturedModalRect(this.guiLeft+76, this.guiTop+17+52-k, 204, 52-k-1, 3, 1 );
        }

        if(true){
            int k = (int)this.getLocalTempatureScaled(51);
            this.drawTexturedModalRect(this.guiLeft+84, this.guiTop+17+52-k, 204, 52-k-1, 3, 1 );
        }
    }

    private float getHeatLeftScaled(int pixels){
        return Math.round(TE.getTemperature()) * pixels/50;
    }

    private float getPressureLeftScaled(int pixels){
        return TE.getPressure()/2000 * pixels;
    }

    private float getCoolentLeftScaled(int pixels){
        return TE.getCoolant() * pixels/10000;
    }

    private float getLocalPressureScaled(int pixels){
        return TE.getLocalPressure()/2000 * pixels;
    }

    private float getLocalTempatureScaled(int pixels){
        return Math.round(TE.getLocalTemperature()) * pixels/50;
    }

}
