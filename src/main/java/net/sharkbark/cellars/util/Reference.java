package net.sharkbark.cellars.util;

import net.dries007.tfc.api.capability.food.FoodTrait;

public class Reference {

    static public final String MOD_ID = "ce";
    static public final String NAME = "Cellars";
    static public final String VERSION = "1.0";
    static public final String ACCEPTED_VERSIONS = "[1.12.2]";
    static public final String CLIENT_PROXY_CLASS = "net.sharkbark.cellars.proxy.ClientProxy";
    static public final String COMMON_PROXY_CLASS = "net.sharkbark.cellars.proxy.ClientProxy";

    public static final int GUI_CELLAR_SHELF = 1;
    public static final int GUI_ICE_BUNKER = 2;

    public static final FoodTrait COOL = new FoodTrait("sharkCool", 0.8F);
    public static final FoodTrait ICY = new FoodTrait("sharkIcy", 0.2F);
    public static final FoodTrait FREEZING = new FoodTrait("sharkIcle", 0.05F);

}
