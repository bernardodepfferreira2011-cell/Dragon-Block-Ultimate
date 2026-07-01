package net.dragonultimate;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.dragonultimate.component.ModDataComponents;
import net.dragonultimate.keybind.KeybindHandler;
import net.dragonultimate.keybind.ModKeybinds;
import net.dragonultimate.models.RaceSkin;
import net.dragonultimate.models.render.RaceSkinRender;
import net.dragonultimate.save.SaveAuraColor;
import net.dragonultimate.save.SaveRaceSkin;
import net.dragonultimate.shader.AuraShaderManager;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(DragonBlockUltimate.MOD_ID)
public class DragonBlockUltimate {
    public static final String MOD_ID = "dragonblockultimate";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DragonBlockUltimate(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModConfiguredFeatures.register(modEventBus);
        ModDataComponents.register(modEventBus);
        SaveRaceSkin.ATTACHMENT_TYPES.register(modEventBus);
        SaveAuraColor.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener(ModKeybinds::register);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(KeybindHandler::onClientTick);
        NeoForge.EVENT_BUS.addListener(SaveRaceSkin::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(SaveRaceSkin::onPlayerRespawn);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {}

        @SubscribeEvent
        public static void onRegisterShaders(net.neoforged.neoforge.client.event.RegisterShadersEvent event) throws java.io.IOException {
            AuraShaderManager.onRegisterShaders(event);
        }

        @SubscribeEvent
        public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(RaceSkin.LAYER, RaceSkin::createBodyLayer);
        }

        @SubscribeEvent
        public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
            for (PlayerSkin.Model skin : event.getSkins()) {
                PlayerRenderer renderer = event.getSkin(skin);
                if (renderer == null) continue;
                RaceSkin model = new RaceSkin(event.getEntityModels().bakeLayer(RaceSkin.LAYER));
                renderer.addLayer(new RaceSkinRender(renderer, model));
            }
        }
    }
}
