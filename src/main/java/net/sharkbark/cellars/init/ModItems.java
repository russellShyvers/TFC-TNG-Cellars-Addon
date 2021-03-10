package net.sharkbark.cellars.init;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.types.DefaultMetals;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import net.sharkbark.cellars.items.ItemIceSaw;
import net.sharkbark.cellars.items.ItemIceShard;
import net.sharkbark.cellars.items.ItemToolHead;
import net.sharkbark.cellars.util.Reference;

import java.util.ArrayList;
import java.util.List;

public class ModItems {

    public static final List<Item> ITEMS = new ArrayList<Item>();

    @ObjectHolder(Reference.MOD_ID + ":bronze_ice_saw")
    public static final Item BRONZE_ICE_SAW = null;
    @ObjectHolder(Reference.MOD_ID + ":bismuth_bronze_ice_saw")
    public static final Item BISMUTH_BRONZE_ICE_SAW = null;
    @ObjectHolder(Reference.MOD_ID + ":black_bronze_ice_saw")
    public static final Item BLACK_BRONZE_ICE_SAW = null;
    @ObjectHolder(Reference.MOD_ID + ":wrought_iron_ice_saw")
    public static final Item WROUGHT_IRON_ICE_SAW = null;
    @ObjectHolder(Reference.MOD_ID + ":steel_ice_saw")
    public static final Item STEEL_ICE_SAW = null;
    @ObjectHolder(Reference.MOD_ID + ":black_steel_ice_saw")
    public static final Item BLACK_STEEL_ICE_SAW = null;
    @ObjectHolder(Reference.MOD_ID + ":red_steel_ice_saw")
    public static final Item RED_STEEL_ICE_SAW = null;
    @ObjectHolder(Reference.MOD_ID + ":blue_steel_ice_saw")
    public static final Item BLUE_STEEL_ICE_SAW = null;

    @ObjectHolder(Reference.MOD_ID + ":bronze_ice_saw_head")
    public static final Item BRONZE_ICE_SAW_HEAD = null;
    @ObjectHolder(Reference.MOD_ID + ":bismuth_bronze_ice_saw_head")
    public static final Item BISMUTH_BRONZE_ICE_SAW_HEAD = null;
    @ObjectHolder(Reference.MOD_ID + ":black_bronze_ice_saw_head")
    public static final Item BLACK_BRONZE_ICE_SAW_HEAD = null;
    @ObjectHolder(Reference.MOD_ID + ":wrought_iron_ice_saw_head")
    public static final Item WROUGHT_IRON_ICE_SAW_HEAD = null;
    @ObjectHolder(Reference.MOD_ID + ":steel_ice_saw_head")
    public static final Item STEEL_ICE_SAW_HEAD = null;
    @ObjectHolder(Reference.MOD_ID + ":black_steel_ice_saw_head")
    public static final Item BLACK_STEEL_ICE_SAW_HEAD = null;
    @ObjectHolder(Reference.MOD_ID + ":red_steel_ice_saw_head")
    public static final Item RED_STEEL_ICE_SAW_HEAD = null;
    @ObjectHolder(Reference.MOD_ID + ":blue_steel_ice_saw_head")
    public static final Item BLUE_STEEL_ICE_SAW_HEAD = null;

    @ObjectHolder(Reference.MOD_ID + ":ice_shard")
    public static final Item ICE_SHARD = null;
    @ObjectHolder(Reference.MOD_ID + ":packed_ice_shard")
    public static final Item PACKED_ICE_SHARD = null;
    @ObjectHolder(Reference.MOD_ID + ":sea_ice_shard")
    public static final Item SEA_ICE_SHARD = null;

    public static void registerItems(IForgeRegistry<Item> registry) {
        Item[] saws = new Item[]{
            new ItemIceSaw(Metal.BRONZE, "bronze_ice_saw"),
            new ItemIceSaw(Metal.BISMUTH_BRONZE, "bismuth_bronze_ice_saw"),
            new ItemIceSaw(Metal.BLACK_BRONZE, "black_bronze_ice_saw"),
            new ItemIceSaw(Metal.WROUGHT_IRON, "wrought_iron_ice_saw"),
            new ItemIceSaw(Metal.STEEL, "steel_ice_saw"),
            new ItemIceSaw(TFCRegistries.METALS.getValue(DefaultMetals.BLACK_STEEL), "black_steel_ice_saw"),
            new ItemIceSaw(Metal.RED_STEEL, "red_steel_ice_saw"),
            new ItemIceSaw(Metal.BLUE_STEEL, "blue_steel_ice_saw")
        };
        registry.registerAll(saws);
        registry.registerAll(
            new ItemToolHead(Metal.BRONZE, "bronze_ice_saw_head", "icesawBlade"),
            new ItemToolHead(Metal.BISMUTH_BRONZE, "bismuth_bronze_ice_saw_head", "icesawBlade"),
            new ItemToolHead(Metal.BLACK_BRONZE, "black_bronze_ice_saw_head", "icesawBlade"),
            new ItemToolHead(Metal.WROUGHT_IRON, "wrought_iron_ice_saw_head", "icesawBlade"),
            new ItemToolHead(Metal.STEEL, "steel_ice_saw_head", "icesawBlade"),
            new ItemToolHead(TFCRegistries.METALS.getValue(DefaultMetals.BLACK_STEEL), "black_steel_ice_saw_head", "icesawBlade"),
            new ItemToolHead(Metal.RED_STEEL, "red_steel_ice_saw_head", "icesawBlade"),
            new ItemToolHead(Metal.BLUE_STEEL, "blue_steel_ice_saw_head", "icesawBlade"),

            new ItemIceShard("ice_shard"),
            new ItemIceShard("packed_ice_shard"),
            new ItemIceShard("sea_ice_shard")
        );

        for(Item saw : saws) {
            ItemStack stack = new ItemStack(saw, 1, OreDictionary.WILDCARD_VALUE);
            OreDictionary.registerOre("tool", stack);
            OreDictionary.registerOre("damageTypeSlashing", stack);
            OreDictionary.registerOre("icesaw", stack);
        }
    }
}
