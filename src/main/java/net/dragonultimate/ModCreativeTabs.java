package net.dragonultimate;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DragonBlockUltimate.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> DRAGON_BLOCK_ULTIMATE = TABS.register("dragon_block_ultimate", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.dragonblockultimate"))
            .icon(() -> new ItemStack(ModItems.NAMEK_GRASS.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.NAMEK_GRASS.get());
                output.accept(ModItems.NAMEK_LEAVE.get());
                output.accept(ModItems.NAMEK_LOG.get());
                output.accept(ModItems.NAMEK_SAPLING.get());
                output.accept(ModItems.LIGHT_DIRTY.get());
                output.accept(ModItems.DINO_MEAT.get());
                output.accept(ModItems.DINO_MEAT_COOKED.get());
                output.accept(ModItems.SENZU.get());
            })
            .build());

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}
