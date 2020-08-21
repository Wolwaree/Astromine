package com.github.chainmailstudios.astromine.technologies.registry;

import com.github.chainmailstudios.astromine.common.recipe.*;
import com.github.chainmailstudios.astromine.common.recipe.AlloySmeltingRecipe;
import com.github.chainmailstudios.astromine.common.recipe.ElectrolyzingRecipe;
import com.github.chainmailstudios.astromine.common.recipe.FluidMixingRecipe;
import com.github.chainmailstudios.astromine.common.recipe.LiquidGeneratingRecipe;
import com.github.chainmailstudios.astromine.common.recipe.PressingRecipe;
import com.github.chainmailstudios.astromine.common.recipe.SolidGeneratingRecipe;
import com.github.chainmailstudios.astromine.common.recipe.TrituratingRecipe;
import com.github.chainmailstudios.astromine.registry.AstromineRecipeSerializers;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.registry.Registry;

public class AstromineTechnologiesRecipeSerializers extends AstromineRecipeSerializers {
	public static final RecipeSerializer<TrituratingRecipe> TRITURATING = Registry.register(Registry.RECIPE_SERIALIZER, TrituratingRecipe.Serializer.ID, TrituratingRecipe.Serializer.INSTANCE);

	public static final RecipeSerializer<PressingRecipe> PRESSING = Registry.register(Registry.RECIPE_SERIALIZER, PressingRecipe.Serializer.ID, PressingRecipe.Serializer.INSTANCE);

	public static final RecipeSerializer<LiquidGeneratingRecipe> LIQUID_GENERATING = Registry.register(Registry.RECIPE_SERIALIZER, LiquidGeneratingRecipe.Serializer.ID, LiquidGeneratingRecipe.Serializer.INSTANCE);

	public static final RecipeSerializer<ElectrolyzingRecipe> ELECTROLYZING = Registry.register(Registry.RECIPE_SERIALIZER, ElectrolyzingRecipe.Serializer.ID, ElectrolyzingRecipe.Serializer.INSTANCE);

	public static final RecipeSerializer<FluidMixingRecipe> FLUID_MIXING = Registry.register(Registry.RECIPE_SERIALIZER, FluidMixingRecipe.Serializer.ID, FluidMixingRecipe.Serializer.INSTANCE);

	public static final RecipeSerializer<SolidGeneratingRecipe> SOLID_GENERATING = Registry.register(Registry.RECIPE_SERIALIZER, SolidGeneratingRecipe.Serializer.ID, SolidGeneratingRecipe.Serializer.INSTANCE);

	public static final RecipeSerializer<AlloySmeltingRecipe> ALLOY_SMELTING = Registry.register(Registry.RECIPE_SERIALIZER, AlloySmeltingRecipe.Serializer.ID, AlloySmeltingRecipe.Serializer.INSTANCE);

	public static void initialize() {

	}
}
