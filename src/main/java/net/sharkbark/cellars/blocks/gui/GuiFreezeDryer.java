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

        if(mouseX >= guiLeft+61 && mouseX <= guiLeft+79 && mouseY >= guiTop+16 && mouseY <= guiTop+34) {
            if(!TE.getSeal()){
                PacketHandler.packetReq.sendToServer(new FDPacket(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), 0, true));
                //TE.seal();
            }else{
                PacketHandler.packetReq.sendToServer(new FDPacket(TE.getPos().getX(), TE.getPos().getY(), TE.getPos().getZ(), 0, false));
                //TE.unseal();
            }
        } else
        if(mouseX >= guiLeft+141 && mouseX <= guiLeft+159 && mouseY >= guiTop+52 && mouseY <= guiTop+70) {
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

        List<String> infoText = new ArrayList<String>();
        if(mouseX >= guiLeft + 5 && mouseX <= guiLeft + 15 && mouseY >= guiTop + 5 && mouseY <= guiTop + 15) {

            if(ModConfig.isDebugging){
                infoText.add("---Debug---");
                infoText.add("Pump Temperature: " + String.format("%.2f",TE.getTemperature()) + "\u2103");
                infoText.add("Internal Pressure: " + String.format("%.2f",TE.getPressure()));
                infoText.add("External Temperature: " + String.format("%.2f",TE.getLocalTemperature()) + "\u2103");
                infoText.add("External Pressure: " + String.format("%.2f",TE.getLocalPressure()));
                infoText.add("Temperature Delta: " + String.format("%.2f",(TE.getTemperature()-TE.getLocalTemperature())) + "\u2103");
                infoText.add("Pressure Delta: " +  String.format("%.2f",(TE.getPressure()-TE.getLocalPressure())));
                infoText.add("Coolant: " +  String.format("%.2f",TE.getCoolant()));
                infoText.add("Progress: " + (TE.getSealedFor()/ModConfig.sealedDuration)*100 + "%");
                infoText.add("Sealed: " + ((TE.getSeal())?"Yes":"No"));
                infoText.add("Pumping: " + ((TE.getPump())?"Yes":"No"));
                infoText.add("Overheating: " + ((TE.overheating)?"Yes":"No"));
                infoText.add("Overheating Ticks: " + TE.overheatTick);
                infoText.add("Power Level: " + TE.getPower());
                infoText.add("Max Pressure: " +(ModConfig.seaLevelPressure+ModConfig.pressureChange*(256-ModConfig.seaLevel)));
                infoText.add("------------");
                infoText.add("-- Config --");
                infoText.add("------------");
                infoText.add("Sea Level: " + ModConfig.seaLevel);
                infoText.add("Sea Pressure: " + ModConfig.seaLevelPressure);
                infoText.add("Work Per Power: " + ModConfig.workPerPower);
                infoText.add("Heat Per Power: " + ModConfig.heatPerPower);
                infoText.add("Change in Pressure: " + ModConfig.pressureChange);
                infoText.add("Dissipation: " + ModConfig.temperatureDissipation);
                infoText.add("Temperature Max: " + ModConfig.maxTemp + "\u2103");
                infoText.add("Coolant Max: " + ModConfig.coolantMax);
                infoText.add("Initialized: " + TE.initialized);
            } else {
                infoText.add("---Info---");
                infoText.add("Temperature: " +  String.format("%.2f",TE.getTemperature()) + "\u2103");
                infoText.add("Pressure: " +  String.format("%.2f",TE.getPressure()));
                infoText.add("Coolant: " +  String.format("%.2f",TE.getCoolant()));
                infoText.add("Progress: " + (TE.getSealedFor()/ModConfig.sealedDuration)*100 + "%");
                infoText.add("----------");
                infoText.add("Sealed: " + ((TE.getSeal())?"Yes":"No"));
                infoText.add("Pumping: " + ((TE.getPump())?"Yes":"No"));
                infoText.add("Power Level: " + TE.getPower());
            }

            this.drawHoveringText(infoText, mouseX-guiLeft, mouseY-guiTop);
            return;
        }

        // Freeze Drier Seal
        if(mouseX >= guiLeft + 62 && mouseX <= guiLeft + 78 && mouseY >= guiTop + 17 && mouseY <= guiTop + 33) {

            if(TE.getSeal()) {
                infoText.add("Unseal Chamber");
            }else{
                infoText.add("Seal Chamber");
            }

            this.drawHoveringText(infoText, mouseX-guiLeft, mouseY-guiTop);
            return;
        }

        // Freeze Drier Snow Flake
        if( (mouseX >= guiLeft + 73 && mouseX <= guiLeft + 103 && mouseY >= guiTop + 36 && mouseY <= guiTop + 50)
                ||(mouseX >= guiLeft + 80 && mouseX <= guiLeft + 96 && mouseY >= guiTop + 28 && mouseY <= guiTop + 58) ) {

            if(ModConfig.isDebugging){
                infoText.add("Progress: " + String.format("%d",TE.getSealedFor()) + "%");
                infoText.add("Ticks Vacuum Sealed: " + String.format("%.2f",TE.getSealedTicks()) + " ticks");
            } else {
                infoText.add("Progress: " + String.format("%d",TE.getSealedFor()) + "%");
            }

            this.drawHoveringText(infoText, mouseX-guiLeft, mouseY-guiTop);
            return;
        }

        // Seal
        if(mouseX >= guiLeft + 125 && mouseX <= guiLeft + 129 && mouseY >= guiTop + 18 && mouseY <= guiTop + 69) {

            if(ModConfig.isDebugging){
                infoText.add("Vacuum: " + String.format("%.5f",TE.getPressure()));
                infoText.add("External Pressure: " + String.format("%.5f",TE.getLocalPressure()));
            } else {
                infoText.add("Vacuum: " +  String.format("%.2f",TE.getPressure()));
                infoText.add("External Pressure: " + String.format("%.2f",TE.getLocalPressure()));
            }

            this.drawHoveringText(infoText, mouseX-guiLeft, mouseY-guiTop);
            return;
        }

        // Heat
        if(mouseX >= guiLeft + 133 && mouseX <= guiLeft + 137 && mouseY >= guiTop + 18 && mouseY <= guiTop + 69) {

            if(ModConfig.isDebugging){
                infoText.add("Heat: " + String.format("%.5f",TE.getTemperature()) + "\u2103");
                infoText.add("External Temperature: " + String.format("%.5f",TE.getLocalTemperature()) + "\u2103");
            } else {
                infoText.add("Heat: " +  String.format("%.2f",TE.getTemperature()) + "\u2103");
                infoText.add("External Temperature: " + String.format("%.2f",TE.getLocalTemperature()) + "\u2103");
            }

            this.drawHoveringText(infoText, mouseX-guiLeft, mouseY-guiTop);
            return;
        }

        // Pump Power
        if(mouseX >= guiLeft + 141 && mouseX <= guiLeft + 158 && mouseY >= guiTop + 53 && mouseY <= guiTop + 70) {

            if(TE.getPump()) {
                infoText.add("Stop Pump");
            }else{
                infoText.add("Start Pump");
            }

            this.drawHoveringText(infoText, mouseX-guiLeft, mouseY-guiTop);
            return;
        }

        // Coolant
        if(mouseX >= guiLeft + 163 && mouseX <= guiLeft + 167 && mouseY >= guiTop + 18 && mouseY <= guiTop + 69) {

            if(ModConfig.isDebugging){
                infoText.add("Coolant: " + String.format("%.5f",TE.getCoolant()));
            } else {
                infoText.add("Coolant: " +  String.format("%.1f",(TE.getCoolant()/ModConfig.coolantMax))+ "%");
            }

            this.drawHoveringText(infoText, mouseX-guiLeft, mouseY-guiTop);
            return;
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
            this.drawTexturedModalRect(this.guiLeft+125, this.guiTop+17+52-k, 180, 52-k-1, 5, k+1 );
        }

        if(true){
            int k = (int)this.getHeatLeftScaled(51);
            this.drawTexturedModalRect(this.guiLeft+133, this.guiTop+17+52-k, 188, 52-k-1, 5, k+1 );
        }

        if(true){
            int k = (int)this.getCoolentLeftScaled(51);
            this.drawTexturedModalRect(this.guiLeft+163, this.guiTop+17+52-k, 196, 52-k-1, 5, k+1 );
        }

        if(true){
            int k = (int)this.getLocalPressureScaled(51);
            this.drawTexturedModalRect(this.guiLeft+126, this.guiTop+17+52-k, 204, 52-k-1, 3, 1 );
        }

        if(true){
            int k = (int)this.getLocalTempatureScaled(51);
            this.drawTexturedModalRect(this.guiLeft+134, this.guiTop+17+52-k, 204, 52-k-1, 3, 1 );
        }

        if(true){
            int k = (int)this.getProgressScaled(27);
            this.drawTexturedModalRect(this.guiLeft+74, this.guiTop+28+28-k, 180, 84-k-1, 28, k+1 );
        }

        if(TE.getSeal()){
            this.drawTexturedModalRect(this.guiLeft+61, this.guiTop+16, 211, 0, 18, 18);
        } else {
            this.drawTexturedModalRect(this.guiLeft+61, this.guiTop+16, 211, 22, 18, 18);
        }

        if(TE.getPump()){
            this.drawTexturedModalRect(this.guiLeft+141, this.guiTop+52, 211, 0, 18, 18);
        } else {
            this.drawTexturedModalRect(this.guiLeft+141, this.guiTop+52, 211, 22, 18, 18);
        }

    }

    private float getProgressScaled(int pixels) {
        return TE.getSealedTicks() * pixels/ModConfig.sealedDuration;
    }

    private float getHeatLeftScaled(int pixels){
        return Math.round(TE.getTemperature()) * pixels/ModConfig.maxTemp;
    }

    private float getPressureLeftScaled(int pixels){
        return (float)TE.getPressure() * pixels/(ModConfig.seaLevelPressure+ModConfig.pressureChange*(256-ModConfig.seaLevel));
    }

    private float getCoolentLeftScaled(int pixels){
        return (float)TE.getCoolant() * pixels/ModConfig.coolantMax;
    }

    private float getLocalPressureScaled(int pixels){
        return (float)TE.getLocalPressure() * pixels/(ModConfig.seaLevelPressure+ModConfig.pressureChange*(256-ModConfig.seaLevel));
    }

    private float getLocalTempatureScaled(int pixels){
        return Math.round(TE.getLocalTemperature()) * pixels/ModConfig.maxTemp;
    }

}
