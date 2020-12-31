package net.sharkbark.cellars;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModConfig {
    public static boolean isDebugging;
    public static float coolantConsumptionMultiplier;
    public static int iceHouseTemperature;
    public static float coolMod;
    public static float icyMod;
    public static float icleMod;
    public static boolean specialIceTraits;
    public static boolean tempMonthAvg;

    public static void loadConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());

        config.load();

        config.setCategoryComment(Configuration.CATEGORY_GENERAL,
                "Debug: Will enable all debug text. Beware will spam console." +
                        "\nSpecial Ice Traits: Makes using sea ice and packed ice effect temperature of the cellar." +
                        "\nMonth Average Temperature: This will cause the temperature calculation to be based on the average temperature of the month. Instead of actual current temperature" +
                        "\nTemperatureIceHouse: Is the minimum value the ice house can make it with out negative temperatures outside. Special Ice Traits do not take this into account.");




        isDebugging = config.get(Configuration.CATEGORY_GENERAL, "Debug", false).getBoolean(false);
        specialIceTraits = config.get(Configuration.CATEGORY_GENERAL, "SpecialIceTraits", false).getBoolean(false);
        tempMonthAvg = config.get(Configuration.CATEGORY_GENERAL, "MonthAvgTemp", false).getBoolean(false);
        iceHouseTemperature = config.get(Configuration.CATEGORY_GENERAL, "TemperatureIceHouse", 1).getInt(1);

        Property coolantConsumptionMultiplierProperty = config.get(Configuration.CATEGORY_GENERAL, "CoolantConsumptionMultiplier", 100);
        coolantConsumptionMultiplierProperty.setComment("The multiplier 100 is 1.0, 123 is 1.23\t:\tIs used to effect the fuel consumption rate.");
        coolantConsumptionMultiplier = (float) (0.01 * coolantConsumptionMultiplierProperty.getInt());

        Property coolModProperty = config.get(Configuration.CATEGORY_GENERAL, "coolMod", 800);
        coolModProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tCurrently none functional till logic rewrite");
        coolMod = (float) (0.001 * coolantConsumptionMultiplierProperty.getInt());
        Property icyModProperty = config.get(Configuration.CATEGORY_GENERAL, "icyMod", 300);
        icyModProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tCurrently none functional till logic rewrite");
        icyMod = (float) (0.001 * coolantConsumptionMultiplierProperty.getInt());
        Property icleModProperty = config.get(Configuration.CATEGORY_GENERAL, "icleMod", 100);
        icleModProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tCurrently none functional till logic rewrite");
        icleMod = (float) (0.001 * coolantConsumptionMultiplierProperty.getInt());

        config.save();
    }
}
