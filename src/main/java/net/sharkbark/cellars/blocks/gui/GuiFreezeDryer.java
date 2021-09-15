package net.sharkbark.cellars.blocks.gui;

import net.dries007.tfc.client.gui.GuiContainerTE;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.sharkbark.cellars.ModConfig;
import net.sharkbark.cellars.blocks.tileentity.TEFreezeDryer;
import net.sharkbark.cellars.util.Reference;
import net.sharkbark.cellars.util.handlers.FDPacket;
import net.sharkbark.cellars.util.handlers.PacketHandler;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GuiFreezeDryer extends GuiContainerTE<TEFreezeDryer> {

    public static final ResourceLocation FREEZE_DRYER_BACKGROUND = new ResourceLocation(Reference.MOD_ID, "textures/gui/freeze_dryer.png");
    private final String translationKey;
    private static TEFreezeDryer TE;
    private final InventoryPlayer playerInventory;

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

        if(mouseX >= guiLeft+101 && mouseX <= guiLeft+127 && mouseY >= guiTop+15 && mouseY <= guiTop+25) {
            if(!TE.getSeal()){
                PacketHandler.packetReq.sendToServer(new FDPacket(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), 0, true));
                //TE.seal();
            }else{
                PacketHandler.packetReq.sendToServer(new FDPacket(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), 0, false));
                //TE.unseal();
            }
        } else
        if(mouseX >= guiLeft+101 && mouseX <= guiLeft+127 && mouseY >= guiTop+30 && mouseY <= guiTop+40) {
            if((TE.getSeal() && TE.getPower() > 0) || TE.getPump()) {
                if(!TE.getPump()){
                    PacketHandler.packetReq.sendToServer(new FDPacket(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), 1, true));
                    //TE.startPump();
                }else{
                    PacketHandler.packetReq.sendToServer(new FDPacket(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), 1, false));
                    //TE.stopPump();
                }
            }
        }
    }


    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String name = I18n.format(translationKey + ".name");
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 00000000);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize-92, 00000000);

        if(TE.getSeal()) {
            IItemHandler handler = (this.tile).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing) null);
            if (handler != null) {
                GL11.glDisable(2929);

                for (int slotId = 0; slotId < handler.getSlots() - 1; ++slotId) {
                    this.drawSlotOverlay(this.inventorySlots.getSlot(slotId));
                }

                GL11.glEnable(2929);
            }
        }

        if(mouseX >= guiLeft + 5 && mouseX <= guiLeft + 15 && mouseY >= guiTop + 5 && mouseY <= guiTop + 15) {
            List<String> infoText = new ArrayList<String>();

            if(ModConfig.isDebugging){
                infoText.add("---Debug---");
                infoText.add("Pump Temperature: " + TE.getTemperature());
                infoText.add("Internal Pressure: " + TE.getPressure());
                infoText.add("External Temperature: " + TE.getLocalTemperature());
                infoText.add("External Pressure: " + TE.getLocalPressure());
                infoText.add("Temperature Delta: " + (TE.getTemperature()-TE.getLocalTemperature()));
                infoText.add("Pressure Delta: " + (TE.getPressure()-TE.getLocalPressure()));
                infoText.add("Coolant: " + TE.getCoolant());
                infoText.add("Power Level: " + TE.getPower());
                infoText.add("Sealed: " + TE.getSeal());
                infoText.add("Pumping: " + TE.getPump());
                infoText.add("------------");
                infoText.add("-- Config --");
                infoText.add("------------");
                infoText.add("Sea Level: " + ModConfig.seaLevel);
                infoText.add("Sea Pressure: " + ModConfig.seaLevelPressure);
                infoText.add("Work Per Power: " + ModConfig.workPerPower);
                infoText.add("Heat Per Power: " + ModConfig.heatPerPower);
                infoText.add("Change in Pressure: " + ModConfig.pressureChange);
                infoText.add("Dissipation: " + ModConfig.temperatureDissipation);
            } else {
                infoText.add("---Info---");
                infoText.add("Temperature: " + TE.getTemperature());
                infoText.add("Pressure: " + TE.getPressure());
                infoText.add("Coolant: " + TE.getCoolant());
                infoText.add("----------");
                infoText.add("Sealed: " + TE.getSeal());
                infoText.add("Pumping: " + TE.getPump());
                infoText.add("Power Level: " + TE.getPower());
            }

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

        if(TE.getSeal()){
            this.drawTexturedModalRect(this.guiLeft+102, this.guiTop+16, 211, 0, 26, 10);
        } else {
            this.drawTexturedModalRect(this.guiLeft+102, this.guiTop+16, 211, 11, 26, 10);
        }

        if(TE.getPump()){
            this.drawTexturedModalRect(this.guiLeft+102, this.guiTop+30, 211, 22, 26, 10);
        } else {
            this.drawTexturedModalRect(this.guiLeft+102, this.guiTop+30, 211, 33, 26, 10);
            if(TE.getSeal()){
                this.drawTexturedModalRect(this.guiLeft+97, this.guiTop+31, 211, 44, 4, 8);
            }else{
                this.drawTexturedModalRect(this.guiLeft+97, this.guiTop+31, 221, 44, 4, 8);
            }
        }

    }

    private float getHeatLeftScaled(int pixels){
        return Math.round(TE.getTemperature()) * pixels/ModConfig.maxTemp;
    }

    private float getPressureLeftScaled(int pixels){
        return TE.getPressure()/((256-ModConfig.seaLevel)*ModConfig.pressureChange+ModConfig.seaLevelPressure) * pixels;
    }

    private float getCoolentLeftScaled(int pixels){
        return TE.getCoolant() * pixels/ModConfig.coolantMax;
    }

    private float getLocalPressureScaled(int pixels){
        return TE.getLocalPressure()/((256-ModConfig.seaLevel)*ModConfig.pressureChange+ModConfig.seaLevelPressure) * pixels;
    }

    private float getLocalTempatureScaled(int pixels){
        return Math.round(TE.getLocalTemperature()) * pixels/ModConfig.maxTemp;
    }

}
