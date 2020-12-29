package net.sharkbark.cellars.foods;

import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodTrait;
import net.minecraft.item.ItemStack;
import net.sharkbark.cellars.ModConfig;

import javax.annotation.Nonnull;

public class CellarTrait extends FoodTrait {

    public static final CellarTrait COOL;
    public static final CellarTrait ICY;
    public static final CellarTrait FREEZING;

    static
    {
        COOL = new CellarTrait("ceCool", ModConfig.coolMod); //Lower then outside temp
        ICY = new CellarTrait("Icy", ModConfig.icyMod);   //Considerably lower then outside temp
        FREEZING = new CellarTrait("Icle", ModConfig.icleMod);   //SubZero
    }

    public CellarTrait(@Nonnull String name, float decayModifier) {
        super(name, decayModifier, true);
        //FoodTrait.getTraits().put(name, this);
        //def >> (ItemStack stack) = CapabilityFood.applyTrait(stack, this);
        //def << (ItemStack stack) = CapabilityFood.removeTrait(stack, this);
    }

    public static void applyTrait(ItemStack stack, CellarTrait trait){
        CapabilityFood.applyTrait(stack, trait);
    }

    public static void removeTrait(ItemStack stack, CellarTrait trait){
        CapabilityFood.removeTrait(stack, trait);
    }


}
