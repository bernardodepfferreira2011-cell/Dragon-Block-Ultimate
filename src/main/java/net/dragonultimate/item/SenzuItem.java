package net.dragonultimate.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SenzuItem extends Item {
    public SenzuItem() {
        super(new Item.Properties()
                .stacksTo(16)
                .food(new FoodProperties.Builder()
                        .nutrition(20)
                        .saturationModifier(2.0F)
                        .build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);

        livingEntity.heal(livingEntity.getMaxHealth());

        if (livingEntity instanceof Player player) {
            player.getFoodData().eat(20, 20.0F);
        }

        return result;
    }
}
