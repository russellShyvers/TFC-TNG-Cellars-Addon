package net.sharkbark.cellars;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.sharkbark.cellars.proxy.CommonProxy;
import net.sharkbark.cellars.util.CellarsTab;
import net.sharkbark.cellars.util.Reference;
import net.sharkbark.cellars.util.handlers.RegistryHandler;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
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
        RegistryHandler.initRegistries();
    }
    @Mod.EventHandler
    public void PostInit(FMLPostInitializationEvent event)
    {

    }

}
