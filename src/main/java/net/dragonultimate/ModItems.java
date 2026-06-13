package net.dragonultimate;

import net.dragonultimate.item.CustomFoodItem;
import net.dragonultimate.item.SenzuItem;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static net.minecraft.core.registries.Registries.ITEM;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ITEM, DragonBlockUltimate.MOD_ID);

    public static final DeferredHolder<Item, Item> NAMEK_GRASS = ITEMS.register("namek_grass",
            () -> new BlockItem(ModBlocks.NAMEK_GRASS.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> NAMEK_LEAVE = ITEMS.register("namek_leave",
            () -> new BlockItem(ModBlocks.NAMEK_LEAVE.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> NAMEK_LOG = ITEMS.register("namek_log",
            () -> new BlockItem(ModBlocks.NAMEK_LOG.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> NAMEK_SAPLING = ITEMS.register("namek_sapling",
            () -> new BlockItem(ModBlocks.NAMEK_SAPLING.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> LIGHT_DIRTY = ITEMS.register("light_dirty",
            () -> new BlockItem(ModBlocks.LIGHT_DIRTY.get(), new Item.Properties()));

    public static final DeferredHolder<Item, Item> DINO_MEAT = ITEMS.register("dino_meat",
            () -> new CustomFoodItem(new FoodProperties.Builder()
                    .nutrition(4)
                    .saturationModifier(0.3F)
                    .build(), 0.15F, 2));

    public static final DeferredHolder<Item, Item> DINO_MEAT_COOKED = ITEMS.register("dino_meat_cooked",
            () -> new CustomFoodItem(new FoodProperties.Builder()
                    .nutrition(8)
                    .saturationModifier(0.6F)
                    .build(), 0.30F, 4));

    public static final DeferredHolder<Item, Item> SENZU = ITEMS.register("senzu", SenzuItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
