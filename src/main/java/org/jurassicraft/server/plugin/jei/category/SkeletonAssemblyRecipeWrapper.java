package org.jurassicraft.server.plugin.jei.category;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jurassicraft.server.entity.EntityHandler;
import org.jurassicraft.server.item.DisplayBlockItem;
import org.jurassicraft.server.item.FossilItem;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.plugin.jei.category.ingredient.SkeletonInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SkeletonAssemblyRecipeWrapper implements IRecipeWrapper {
    private final SkeletonInput input;

    public SkeletonAssemblyRecipeWrapper(SkeletonInput input) {
        this.input = input;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        String[][] recipe = this.input.dinosaur.getRecipe();
        Map<String, FossilItem> fossils = this.input.fresh ? ItemHandler.FRESH_FOSSILS : ItemHandler.FOSSILS;
        int id = EntityHandler.getDinosaurId(this.input.dinosaur);

        List<ItemStack> inputs = new ArrayList<>(recipe.length);
        for (String[] row : recipe) {
            for (String column : row) {
                if (column != null && !column.isEmpty()) {
                    inputs.add(new ItemStack(fossils.get(column), 1, id));
                } else {
                    inputs.add(null);
                }
            }
        }
        ingredients.setInputs(ItemStack.class, inputs);

        ItemStack output = new ItemStack(ItemHandler.DISPLAY_BLOCK, 1, DisplayBlockItem.getMetadata(id, this.input.fresh ? 2 : 1, true));
        ingredients.setOutput(ItemStack.class, output);
    }

    @Override
    public List getInputs() {
        return Collections.emptyList();
    }

    @Override
    public List getOutputs() {
        return Collections.emptyList();
    }

    @Override
    public List<FluidStack> getFluidInputs() {
        return Collections.emptyList();
    }

    @Override
    public List<FluidStack> getFluidOutputs() {
        return Collections.emptyList();
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
    }

    @Override
    public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
