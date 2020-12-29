package net.sharkbark.cellars;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModConfig {
    public static boolean isDebugging;
    public static float coolantConsumptionMultiplier;
    public static int iceHouseTemperature;
    public static boolean fastComplete;
    public static float coolMod;
    public static float icyMod;
    public static float icleMod;
    public static boolean enable;

    public static void loadConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());

        config.load();

        isDebugging = config.get(Configuration.CATEGORY_GENERAL, "Debug", false).getBoolean(false);
        fastComplete = config.get(Configuration.CATEGORY_GENERAL, "FastCheck", false).getBoolean(false);
        iceHouseTemperature = config.get(Configuration.CATEGORY_GENERAL, "TemperatureIceHouse", 1).getInt(1);

        Property coolantConsumptionMultiplierProperty = config.get(Configuration.CATEGORY_GENERAL, "CoolantConsumptionMultiplier", 100);
        coolantConsumptionMultiplierProperty.setComment("The multiplier 100 is 1.0, 123 is 1.23");
        coolantConsumptionMultiplier = (float) (0.01 * coolantConsumptionMultiplierProperty.getInt());

        Property coolModProperty = config.get(Configuration.CATEGORY_GENERAL, "coolMod", 800);
        coolModProperty.setComment("1000 is 1.00, 1230 is 1.23");
        coolMod = (float) (0.001 * coolantConsumptionMultiplierProperty.getInt());
        Property icyModProperty = config.get(Configuration.CATEGORY_GENERAL, "icyMod", 300);
        icyModProperty.setComment("1000 is 1.00, 1230 is 1.23");
        icyMod = (float) (0.001 * coolantConsumptionMultiplierProperty.getInt());
        Property icleModProperty = config.get(Configuration.CATEGORY_GENERAL, "icleMod", 100);
        icleModProperty.setComment("1000 is 1.00, 1230 is 1.23");
        icleMod = (float) (0.001 * coolantConsumptionMultiplierProperty.getInt());

        Property enableProperty = config.get(Configuration.CATEGORY_GENERAL, "enabledProperty", true);
        enableProperty.setComment("Disables Cellar Shelves and Bunker when set to false.");
        enable = (boolean) (true);

        config.save();
    }
}
