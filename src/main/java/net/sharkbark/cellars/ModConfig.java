package net.sharkbark.cellars;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModConfig {
    public static boolean isDebugging;
    public static float coolantConsumptionMultiplier;
    public static int iceHouseTemperature;
    public static float coolMod;
    public static float icyMod;
    public static float icleMod;
    public static float dryMod;
    public static boolean specialIceTraits;
    public static boolean tempMonthAvg;
    public static boolean disableShards;
    public static int packedIceCoolant;
    public static int seaIceCoolant;
    public static int iceCoolant;
    public static int snowCoolant;
    public static int snowBallCoolant;
    public static int coolMaxThreshold;
    public static int frozenMaxThreshold;
    public static int icyMaxThreshold;

    public static int seaLevel;
    public static float seaLevelPressure;
    public static float workPerPower;
    public static float heatPerPower;
    public static float pressureChange;

    public static void loadConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());

        config.load();

        config.setCategoryComment(Configuration.CATEGORY_GENERAL,
                "###BEWARE CHANGING TRAIT MODIFIERS CAN SPOIL FOOD STORED IN SHELVES###"+
                        "\nDebug: Will enable all debug text. Beware will spam console." +
                        "\nSpecial Ice Traits: Makes using sea ice and packed ice effect temperature of the cellar." +
                        "\nMonth Average Temperature: This will cause the temperature calculation to be based on the average temperature of the month. Instead of actual current temperature" +
                        "\nTemperatureIceHouse: Is the minimum value the ice house can make it with out negative temperatures outside. Special Ice Traits do not take this into account." +
                        "\nDisableShards: Turning this value to true will change Ice Saw drops to ice blocks instead of shards.");

        isDebugging = config.get(Configuration.CATEGORY_GENERAL, "Debug", false).getBoolean(false);
        specialIceTraits = config.get(Configuration.CATEGORY_GENERAL, "SpecialIceTraits", false).getBoolean(false);
        tempMonthAvg = config.get(Configuration.CATEGORY_GENERAL, "MonthAvgTemp", false).getBoolean(false);
        iceHouseTemperature = config.get(Configuration.CATEGORY_GENERAL, "TemperatureIceHouse", 1).getInt(1);
        disableShards = config.get(Configuration.CATEGORY_GENERAL, "DisableShards", false).getBoolean(false);

        Property coolantConsumptionMultiplierProperty = config.get(Configuration.CATEGORY_GENERAL, "CoolantConsumptionMultiplier", 100);
        coolantConsumptionMultiplierProperty.setComment("The multiplier 100 is 1.0, 123 is 1.23\t:\tIs used to effect the fuel consumption rate.");
        coolantConsumptionMultiplier = (float) (0.01 * coolantConsumptionMultiplierProperty.getInt());

        Property coolModProperty = config.get(Configuration.CATEGORY_GENERAL, "coolMod", 800);
        coolModProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tCool Trait Modifier");
        coolMod = (float) (0.001 * coolModProperty.getInt());
        Property icyModProperty = config.get(Configuration.CATEGORY_GENERAL, "icyMod", 500);
        icyModProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tIcy Trait Modifier");
        icyMod = (float) (0.001 * icyModProperty.getInt());
        Property icleModProperty = config.get(Configuration.CATEGORY_GENERAL, "frozenMod", 250);
        icleModProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tFrozen Trait Modifier");
        icleMod = (float) (0.001 * icleModProperty.getInt());
        Property dryModProperty = config.get(Configuration.CATEGORY_GENERAL, "frozenMod", 250);
        dryModProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tPreserved Trait Modifier for Freeze Dryer");
        dryMod = (float) (0.001 * dryModProperty.getInt());

        Property packedIce = config.get(Configuration.CATEGORY_GENERAL, "packedIce", 60);
        packedIce.setComment("This setting dictates how much coolant you get from a block of Packed Ice or Packed Ice Shards in the Ice Bunker");
        packedIceCoolant = packedIce.getInt();
        Property seaIce = config.get(Configuration.CATEGORY_GENERAL, "seaIce", 180);
        seaIce.setComment("This setting dictates how much coolant you get from a block of Sea Ice or Sea Ice Shards in the Ice Bunker");
        seaIceCoolant = seaIce.getInt();
        Property ice = config.get(Configuration.CATEGORY_GENERAL, "ice", 120);
        ice.setComment("This setting dictates how much coolant you get from a block of Ice or Ice Shards in the Ice Bunker");
        iceCoolant = ice.getInt();
        Property snow = config.get(Configuration.CATEGORY_GENERAL, "snow", 60);
        snow.setComment("This setting dictates how much coolant you get from a block of Snow in the Ice Bunker");
        snowCoolant = snow.getInt();
        Property snowBall = config.get(Configuration.CATEGORY_GENERAL, "snowball", 15);
        snowBall.setComment("This setting dictates how much coolant you get from a block of Snowball in the Ice Bunker");
        snowBallCoolant = snowBall.getInt();

        Property coolMax = config.get(Configuration.CATEGORY_GENERAL, "coolTemperature", 20);
        coolMax.setComment("This is the temperature at which foods will gain a trait.");
        coolMaxThreshold = coolMax.getInt();
        Property icyMax = config.get(Configuration.CATEGORY_GENERAL, "icyTemperature", 5);
        icyMax.setComment("This is the temperature at which foods will go from cool to icy");
        icyMaxThreshold = icyMax.getInt();
        Property frozenMax = config.get(Configuration.CATEGORY_GENERAL, "frozenTemperature", 0);
        frozenMax.setComment("This is the temperature at which foods will go from icy to frozen");
        frozenMaxThreshold = frozenMax.getInt();

        Property seaLevelProperty = config.get(Configuration.CATEGORY_GENERAL, "seaLevel", 64);
        seaLevelProperty.setComment("This is the world sea level height.");
        seaLevel = frozenMax.getInt();

        Property seaLevelPressureProperty = config.get(Configuration.CATEGORY_GENERAL, "seaLevelPressure", 1016);
        seaLevelPressureProperty.setComment("This is the sea level pressure.");
        seaLevelPressure = seaLevelPressureProperty.getInt();

        Property workPerPowerProperty = config.get(Configuration.CATEGORY_GENERAL, "workPerPower", 100);
        workPerPowerProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tWork per redstone power.");
        workPerPower = (float) (0.001 * workPerPowerProperty.getInt());

        Property heatPerPowerProperty = config.get(Configuration.CATEGORY_GENERAL, "heatPerPower", 1000);
        heatPerPowerProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tHeat generated per redstone power.");
        heatPerPower = (float) (0.001 * heatPerPowerProperty.getInt());

        Property pressureChangeProperty = config.get(Configuration.CATEGORY_GENERAL, "pressureChange", 1980);
        pressureChangeProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tPressure change per Y level.");
        pressureChange = (float) (0.001 * pressureChangeProperty.getInt());

        config.save();
    }
}
