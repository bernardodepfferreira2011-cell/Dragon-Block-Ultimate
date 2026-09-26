package net.dragonultimate;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.dragonultimate.component.ModDataComponents;
import net.dragonultimate.keybind.KeybindHandler;
import net.dragonultimate.keybind.ModKeybinds;
import net.dragonultimate.aura.ClientAuraManager;
import net.dragonultimate.aura.SaveAuraData;
import net.dragonultimate.aura.render.AuraRenderLayer;
import net.dragonultimate.models.RaceSkin;
import net.dragonultimate.models.render.LightningRenderLayer;
import net.dragonultimate.network.NetworkHandler;
import net.dragonultimate.save.SaveRaceSkin;
import net.dragonultimate.stats.PlayerManager;
import net.dragonultimate.shader.BloomPipeline;
import net.dragonultimate.shader.LightShaderManager;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.dragonultimate.screen.huds.KiBar;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

@Mod(DragonBlockUltimate.MOD_ID)
public class DragonBlockUltimate {
    public static final String MOD_ID = "dragonblockultimate";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DragonBlockUltimate(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(NetworkHandler::register);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModConfiguredFeatures.register(modEventBus);
        ModDataComponents.register(modEventBus);
        SaveRaceSkin.ATTACHMENT_TYPES.register(modEventBus);
        PlayerManager.ATTACHMENT_TYPES.register(modEventBus);
        SaveAuraData.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener(ModKeybinds::register);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(KeybindHandler::onClientTick);
        NeoForge.EVENT_BUS.addListener(BloomPipeline::onRenderLevelStage);
        NeoForge.EVENT_BUS.addListener(SaveRaceSkin::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(SaveRaceSkin::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(SaveAuraData::onPlayerClone);
        NeoForge.EVENT_BUS.addListener(SaveAuraData::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(SaveAuraData::onStartTracking);
        NeoForge.EVENT_BUS.addListener(ClientAuraManager::onClientTick);
        NeoForge.EVENT_BUS.addListener(PlayerManager::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(NetworkHandler::onPlayerLogin);
        NeoForge.EVENT_BUS.addListener(DragonBlockUltimate::onLivingDamage);
    }

    private static void onLivingDamage(LivingDamageEvent.Post event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide() || !(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        PlayerManager.addTrainerPoints(player, 1);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        private static final KiBar KI_BAR = new KiBar();

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {}

        @SubscribeEvent
        public static void onRegisterShaders(net.neoforged.neoforge.client.event.RegisterShadersEvent event)
            throws java.io.IOException {
            LightShaderManager.register(event);
        }

        @SubscribeEvent
        public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(RaceSkin.LAYER, RaceSkin::createBodyLayer);
        }

        @SubscribeEvent
        public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
            for (PlayerSkin.Model skin : event.getSkins()) {
                PlayerRenderer renderer = event.getSkin(skin);
                if (renderer != null) {
                    renderer.addLayer(new LightningRenderLayer(renderer));
                    renderer.addLayer(new AuraRenderLayer(renderer));
                }
            }
        }

        @SubscribeEvent
        public static void onRenderGui(RenderGuiEvent.Post event) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.player != null) {
                KI_BAR.render(minecraft, event.getGuiGraphics(), minecraft.player);
            }
        }
    }
}
