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
    public static float preservingMod;
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
    public static float temperatureDissipation;
    public static float targetPressure;
    public static int sealedDuration;
    public static float coolantMax;
    public static int maxTemp;

    public static void loadConfig(FMLPreInitializationEvent event) {
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());

        config.load();

        config.setCategoryComment(Configuration.CATEGORY_GENERAL,
                "###BEWARE CHANGING TRAIT MODIFIERS CAN SPOIL FOOD STORED IN SHELVES###"+
                        "\nDebug: Will enable all debug text." +
                        "\nSpecial Ice Traits: Makes using sea ice and packed ice effect temperature of the cellars." +
                        "\nMonth Average Temperature: This will cause the temperature calculation, for cellars, to be based on the average temperature of the month. Instead of actual current temperature" +
                        "\nTemperatureIceHouse: Is the minimum value the ice house can make it with out negative temperatures outside. Special Ice Traits do not take this into account." +
                        "\nDisableShards: Turning this value to true will change Ice Saw drops to ice blocks instead of shards.");

        isDebugging = config.get(Configuration.CATEGORY_GENERAL, "Debug", false).getBoolean(false);
        specialIceTraits = config.get(Configuration.CATEGORY_GENERAL, "SpecialIceTraits", false).getBoolean(false);
        tempMonthAvg = config.get(Configuration.CATEGORY_GENERAL, "MonthAvgTemp", false).getBoolean(false);
        iceHouseTemperature = config.get(Configuration.CATEGORY_GENERAL, "TemperatureIceHouse", 1).getInt(1);
        disableShards = config.get(Configuration.CATEGORY_GENERAL, "DisableShards", false).getBoolean(false);

        Property coolantConsumptionMultiplierProperty = config.get(Configuration.CATEGORY_GENERAL, "CoolantConsumptionMultiplier", 100);
        coolantConsumptionMultiplierProperty.setComment("The multiplier 100 is 1.0, 123 is 1.23\t:\tIs used to effect the coolant consumption rate of the cellars");
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
        Property dryModProperty = config.get(Configuration.CATEGORY_GENERAL, "dryMod", 100);
        dryModProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tPreserved Trait Modifier for Freeze Dryer");
        dryMod = (float) (0.001 * dryModProperty.getInt());
        Property preservingModProperty = config.get(Configuration.CATEGORY_GENERAL, "preservingMod", 900);
        preservingModProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tPreserving Trait Modifier for Freeze Dryer when sealed");
        preservingMod = (float) (0.001 * preservingModProperty.getInt());

        Property packedIce = config.get(Configuration.CATEGORY_GENERAL, "packedIce", 60);
        packedIce.setComment("This setting dictates how much coolant you get from a block of Packed Ice or Packed Ice Shards");
        packedIceCoolant = packedIce.getInt();
        Property seaIce = config.get(Configuration.CATEGORY_GENERAL, "seaIce", 180);
        seaIce.setComment("This setting dictates how much coolant you get from a block of Sea Ice or Sea Ice Shards");
        seaIceCoolant = seaIce.getInt();
        Property ice = config.get(Configuration.CATEGORY_GENERAL, "ice", 120);
        ice.setComment("This setting dictates how much coolant you get from a block of Ice or Ice Shards");
        iceCoolant = ice.getInt();
        Property snow = config.get(Configuration.CATEGORY_GENERAL, "snow", 60);
        snow.setComment("This setting dictates how much coolant you get from a block of Snow");
        snowCoolant = snow.getInt();
        Property snowBall = config.get(Configuration.CATEGORY_GENERAL, "snowball", 15);
        snowBall.setComment("This setting dictates how much coolant you get from a block of Snowball");
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

        Property seaLevelProperty = config.get(Configuration.CATEGORY_GENERAL, "seaLevel", 143);
        seaLevelProperty.setComment("This is the world sea level height.");
        seaLevel = seaLevelProperty.getInt();
        Property seaLevelPressureProperty = config.get(Configuration.CATEGORY_GENERAL, "seaLevelPressure", 1016);
        seaLevelPressureProperty.setComment("This is the sea level pressure.");
        seaLevelPressure = seaLevelPressureProperty.getInt();

        Property workPerPowerProperty = config.get(Configuration.CATEGORY_GENERAL, "workPerPower", 100);
        workPerPowerProperty.setComment("Work per redstone power level each second");
        workPerPower = workPerPowerProperty.getInt();
        Property heatPerPowerProperty = config.get(Configuration.CATEGORY_GENERAL, "heatPerPower", 100);
        heatPerPowerProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tHeat generated per redstone power level");
        heatPerPower = (float) (0.001 * heatPerPowerProperty.getInt());

        Property pressureChangeProperty = config.get(Configuration.CATEGORY_GENERAL, "pressureChange", 1980);
        pressureChangeProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tPressure change per Y level");
        pressureChange = (float) (0.001 * pressureChangeProperty.getInt());

        Property temperatureDissipationProperty = config.get(Configuration.CATEGORY_GENERAL, "temperatureDissipation", 20);
        temperatureDissipationProperty.setComment("1000 is 10.0, 1230 is 12.3\t:\tPercentage of Temperature Delta in heat dissipated per second");
        temperatureDissipation = (float) (0.01 * temperatureDissipationProperty.getInt());

        Property sealedDurationProperty = config.get(Configuration.CATEGORY_GENERAL, "sealedDuration", 120);
        sealedDurationProperty.setComment("Number of seconds at target pressure to preserve.");
        sealedDuration = sealedDurationProperty.getInt();

        Property targetPressureProperty = config.get(Configuration.CATEGORY_GENERAL, "targetPressure", 600);
        targetPressureProperty.setComment("1000 is 1.00, 1230 is 1.23\t:\tTarget pressure to achieve to start preserving");
        targetPressure = (float) (0.001 * targetPressureProperty.getInt());

        Property coolantMaxProperty = config.get(Configuration.CATEGORY_GENERAL, "coolantMax", 6400);
        coolantMaxProperty.setComment("Maximum amount of coolant freeze dryer can store internally");
        coolantMax = coolantMaxProperty.getInt();

        Property maxTempProperty = config.get(Configuration.CATEGORY_GENERAL, "tempMax", 40);
        maxTempProperty.setComment("Maximum temperature of freeze dryer vacuum pump");
        maxTemp = maxTempProperty.getInt();

        config.save();
    }
}
