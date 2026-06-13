package net.dragonultimate.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CustomFoodItem extends Item {
    private final float healPercent;
    private final int hungerBars;

    public CustomFoodItem(FoodProperties foodProperties, float healPercent, int hungerBars) {
        super(new Item.Properties().food(foodProperties));
        this.healPercent = healPercent;
        this.hungerBars = hungerBars;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);

        if (healPercent > 0.0F) {
            float amount = livingEntity.getMaxHealth() * healPercent;
            livingEntity.heal(amount);
        }

        if (livingEntity instanceof Player player && hungerBars > 0) {
            player.getFoodData().eat(hungerBars * 2, 20.0F);
        }

        return result;
    }
}
