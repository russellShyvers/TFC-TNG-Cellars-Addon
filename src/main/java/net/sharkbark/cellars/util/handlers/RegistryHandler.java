package net.sharkbark.cellars.util.handlers;

import net.dries007.tfc.api.recipes.anvil.AnvilRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Metal.ItemType;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.types.DefaultMetals;
import net.dries007.tfc.util.forge.ForgeRule;
import net.dries007.tfc.util.skills.SmithingSkill;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.sharkbark.cellars.Main;
import net.sharkbark.cellars.init.ModBlocks;
import net.sharkbark.cellars.init.ModItems;
import net.sharkbark.cellars.util.IHasModel;
import net.sharkbark.cellars.util.Reference;

@Mod.EventBusSubscriber
public class RegistryHandler {

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(ModItems.ITEMS.toArray(new Item[0]));
        ModItems.registerItems(event.getRegistry());
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> event) {

        event.getRegistry().registerAll(ModBlocks.BLOCKS.toArray(new Block[0]));
        TileEntityHandler.registerTileEntities();

    }

    @SubscribeEvent
    public static void onModelRegister(ModelRegistryEvent event) {

        for(Item item : ModItems.ITEMS){

            if(item instanceof IHasModel){

                ((IHasModel)item).registerModels();

            }

        }

        for(Block block : ModBlocks.BLOCKS){

            if(block instanceof IHasModel){

                ((IHasModel)block).registerModels();

            }

        }

    }

    @SubscribeEvent
    public static void registerAnvilRecipes(RegistryEvent.Register<AnvilRecipe> event) {
        ForgeRule[] iceSawRules = new ForgeRule[] { ForgeRule.HIT_LAST, ForgeRule.UPSET_SECOND_LAST, ForgeRule.DRAW_NOT_LAST };
        event.getRegistry().registerAll(
            new AnvilRecipe(new ResourceLocation(Reference.MOD_ID, "bronze_ice_saw"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.BRONZE, ItemType.DOUBLE_INGOT))),
                new ItemStack(ModItems.BRONZE_ICE_SAW_HEAD), Metal.BRONZE.getTier(), SmithingSkill.Type.TOOLS, iceSawRules),
            new AnvilRecipe(new ResourceLocation(Reference.MOD_ID, "bismuth_bronze_ice_saw"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.BISMUTH_BRONZE, ItemType.DOUBLE_INGOT))),
                new ItemStack(ModItems.BISMUTH_BRONZE_ICE_SAW_HEAD), Metal.BISMUTH_BRONZE.getTier(), SmithingSkill.Type.TOOLS, iceSawRules),
            new AnvilRecipe(new ResourceLocation(Reference.MOD_ID, "black_bronze_ice_saw"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.BLACK_BRONZE, ItemType.DOUBLE_INGOT))),
                new ItemStack(ModItems.BLACK_BRONZE_ICE_SAW_HEAD), Metal.BLACK_BRONZE.getTier(), SmithingSkill.Type.TOOLS, iceSawRules),
            new AnvilRecipe(new ResourceLocation(Reference.MOD_ID, "wrought_iron_ice_saw"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.WROUGHT_IRON, ItemType.DOUBLE_INGOT))),
                new ItemStack(ModItems.WROUGHT_IRON_ICE_SAW_HEAD), Metal.WROUGHT_IRON.getTier(), SmithingSkill.Type.TOOLS, iceSawRules),
            new AnvilRecipe(new ResourceLocation(Reference.MOD_ID, "steel_ice_saw"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.STEEL, ItemType.DOUBLE_INGOT))),
                new ItemStack(ModItems.STEEL_ICE_SAW_HEAD), Metal.STEEL.getTier(), SmithingSkill.Type.TOOLS, iceSawRules),
            new AnvilRecipe(new ResourceLocation(Reference.MOD_ID, "black_steel_ice_saw"), IIngredient.of(new ItemStack(ItemMetal.get(TFCRegistries.METALS.getValue(DefaultMetals.BLACK_STEEL), ItemType.DOUBLE_INGOT))),
                new ItemStack(ModItems.BLACK_STEEL_ICE_SAW_HEAD), TFCRegistries.METALS.getValue(DefaultMetals.BLACK_STEEL).getTier(), SmithingSkill.Type.TOOLS, iceSawRules),
            new AnvilRecipe(new ResourceLocation(Reference.MOD_ID, "red_steel_ice_saw"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.RED_STEEL, ItemType.DOUBLE_INGOT))),
                new ItemStack(ModItems.RED_STEEL_ICE_SAW_HEAD), Metal.RED_STEEL.getTier(), SmithingSkill.Type.TOOLS, iceSawRules),
            new AnvilRecipe(new ResourceLocation(Reference.MOD_ID, "blue_steel_ice_saw"), IIngredient.of(new ItemStack(ItemMetal.get(Metal.BLUE_STEEL, ItemType.DOUBLE_INGOT))),
                new ItemStack(ModItems.BLUE_STEEL_ICE_SAW_HEAD), Metal.BLUE_STEEL.getTier(), SmithingSkill.Type.TOOLS, iceSawRules)
        );
    }

    public static void initRegistries(){
        NetworkRegistry.INSTANCE.registerGuiHandler(Main.INSTANCE, new GuiHandler());
    }

}
