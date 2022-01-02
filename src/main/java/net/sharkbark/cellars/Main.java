package net.sharkbark.cellars;

import akka.event.EventBus;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.capability.player.IPlayerData;
import net.dries007.tfc.compat.patchouli.TFCPatchouliPlugin;
import net.dries007.tfc.network.PacketPlayerDataUpdate;
import net.dries007.tfc.objects.container.CapabilityContainerListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import net.sharkbark.cellars.proxy.CommonProxy;
import net.sharkbark.cellars.util.CellarsTab;
import net.sharkbark.cellars.util.Reference;
import net.sharkbark.cellars.util.handlers.PacketHandler;
import net.sharkbark.cellars.util.handlers.RegistryHandler;
import scala.reflect.internal.AnnotationInfos;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.item.PatchouliItems;

import java.awt.*;
import java.util.List;
import java.util.Map;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, dependencies = "required-after:tfc")
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class Main {

    public static final CellarsTab creativeTab = new CellarsTab();

    @Mod.Instance
    public static Main INSTANCE;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event)
    {
        ModConfig.loadConfig(event);
    }
    @Mod.EventHandler
    public void Init(FMLInitializationEvent event)
    {
        System.out.println("Mod Config for Cellars has Cool Mod as "+ModConfig.coolMod);
        System.out.println("Mod Config for Cellars has Icy Mod as "+ModConfig.icyMod);
        System.out.println("Mod Config for Cellars has Frozen Mod as "+ModConfig.icleMod);
        System.out.println("Mod Config for Cellars has Freeze Dryed Mod as "+ModConfig.dryMod);
        System.out.println("Mod Config for Cellars has Preserving Mod as "+ModConfig.preservingMod);

        RegistryHandler.initRegistries();

        Reference.COOL = new FoodTrait("sharkCool", ModConfig.coolMod);
        Reference.ICY = new FoodTrait("sharkIcy", ModConfig.icyMod);
        Reference.FREEZING = new FoodTrait("sharkIcle", ModConfig.icleMod);
        Reference.DRY = new FoodTrait("sharkDry", ModConfig.dryMod);
        Reference.PRESERVING = new FoodTrait("sharkPreserving", ModConfig.preservingMod);

        Map<String, FoodTrait> tmp = FoodTrait.getTraits();
        for (Map.Entry<String,FoodTrait> entry : tmp.entrySet())
            System.out.println("Key = " + entry.getKey() +
                    ", Value = " + entry.getValue() +
                    ", Decay = " + entry.getValue().getDecayModifier());



    }
    @Mod.EventHandler
    public void PostInit(FMLPostInitializationEvent event)
    {
        Reference.initialized = true;
        PacketHandler.init();
    }

    @SubscribeEvent/*
    public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if(ModConfig.firstJoinBook){
            if (event.getEntity() instanceof EntityPlayerMP) {
                EntityPlayerMP player = (EntityPlayerMP) event.getEntity();

                System.out.println("Checking for player first join.");
                if (player.getEntityData().getInteger("CellarsFirstJoin") != 1) {
                    System.out.println("Joined before, skipping book giving.");
                } else {
                    System.out.println("Setting variable.");
                    player.getEntityData().setInteger("CellarsFirstJoin",1);

                    System.out.println("Attempting to give book.");
                    NBTTagCompound bookData = new NBTTagCompound();
                    bookData.setString("patchouli:book", "cellars:book");
                    ItemStack book = new ItemStack(PatchouliItems.book);
                    book.setTagCompound(bookData);
                    ItemHandlerHelper.giveItemToPlayer(player, book);
                }
            }
        }
    }*/

    public static void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
        if (!ModConfig.firstJoinBook || event.getWorld().isRemote) {
            return;
        }
        if(event.getEntity() instanceof EntityPlayer){
            NBTTagCompound data = event.getEntity().getEntityData();
            NBTTagCompound persistent;
            if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
                data.setTag(EntityPlayer.PERSISTED_NBT_TAG, (persistent = new NBTTagCompound()));
            } else {
                persistent = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
            }

            if (!persistent.hasKey("CellarsFirstJoin")) {
                persistent.setBoolean("CellarsFirstJoin", true);
                try {
                    NBTTagCompound bookData = new NBTTagCompound();
                    bookData.setString("patchouli:book", "cellars:book");
                    ItemStack book = new ItemStack(PatchouliItems.book);
                    book.setTagCompound(bookData);
                    ItemHandlerHelper.giveItemToPlayer((EntityPlayer) event.getEntity(), book);
                }catch (NoClassDefFoundError e){
                    System.out.println("Mod failed to give Patchouli Book to" + event.getEntity());
                }
            }
        }
    }

}
