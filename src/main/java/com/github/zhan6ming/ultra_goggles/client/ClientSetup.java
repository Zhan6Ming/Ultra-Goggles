package com.github.zhan6ming.ultra_goggles.client;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class ClientSetup {

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ClientSetup::onRegisterKeyMappings);
        modEventBus.addListener(ClientSetup::onRegisterAdditional);
        modEventBus.addListener(ClientSetup::onModifyBakingResult);
        modEventBus.addListener(ClientSetup::onLayerRegister);
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.TOGGLE_NIGHTVISION);
        event.register(KeyBindings.OPEN_OFFSET_CONFIG);
    }

    private static void onRegisterAdditional(ModelEvent.RegisterAdditional event) {
        UltraGogglesModel.onRegisterAdditional(event);
    }

    private static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        UltraGogglesModel.onModifyBakingResult(event);
    }

    private static void onLayerRegister(EntityRenderersEvent.RegisterLayerDefinitions event) {
        if (ModList.get().isLoaded("curios")) {
            com.github.zhan6ming.ultra_goggles.compat.curios.CuriosRenderers.onLayerRegister(event);
        }
    }
}
