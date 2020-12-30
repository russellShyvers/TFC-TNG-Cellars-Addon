package net.sharkbark.cellars.foods;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.minecraft.item.ItemStack;
import net.sharkbark.cellars.ModConfig;

import javax.annotation.Nonnull;

public class CellarTrait extends FoodTrait {

    public static final CellarTrait COOL = new CellarTrait("sharkCool", ModConfig.coolMod);
    public static final CellarTrait ICY = new CellarTrait("sharkIcy", ModConfig.icyMod);
    public static final CellarTrait FREEZING = new CellarTrait("sharkIcle", ModConfig.icyMod);

    public CellarTrait(@Nonnull String name, float decayModifier) {
        super(name, decayModifier);
    }

    public CellarTrait(@Nonnull String name, float decayModifier, boolean hasTooltip) {
        super(name, decayModifier, hasTooltip);
    }

    public static void applyTrait(ItemStack stack, FoodTrait trait){
        CapabilityFood.applyTrait(stack, trait);
    }

    public static void removeTrait(ItemStack stack, FoodTrait trait){

        CapabilityFood.removeTrait(stack, trait);
    }


}
