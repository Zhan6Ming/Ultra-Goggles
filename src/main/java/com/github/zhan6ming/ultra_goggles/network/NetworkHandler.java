package com.github.zhan6ming.ultra_goggles.network;

import com.github.zhan6ming.ultra_goggles.UltraGoggles;
import com.github.zhan6ming.ultra_goggles.event.PlayerOffsetData;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.PacketDistributor;

public class NetworkHandler {

    private static final org.slf4j.Logger LOGGER = LogUtils.getLogger();

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(NetworkHandler::onRegisterPayloads);
    }

    private static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(UltraGoggles.MODID).versioned("1");

        registrar.playToServer(
            ToggleNightVisionPacket.TYPE,
            ToggleNightVisionPacket.CODEC,
            NetworkHandler::handleToggleNightVision
        );

        registrar.playToServer(
            SyncOffsetPacket.TYPE,
            SyncOffsetPacket.CODEC,
            NetworkHandler::handleSyncOffsetFromClient
        );

        registrar.playToClient(
            SyncOffsetBroadcastPacket.TYPE,
            SyncOffsetBroadcastPacket.CODEC,
            NetworkHandler::handleSyncOffsetBroadcast
        );
    }

    private static void handleToggleNightVision(ToggleNightVisionPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null) {
                packet.handle((net.minecraft.server.level.ServerPlayer) context.player());
            }
        }).exceptionally(e -> {
            LOGGER.error("Failed to handle toggle night vision packet", e);
            return null;
        });
    }

    private static void handleSyncOffsetFromClient(SyncOffsetPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() != null) {
                PlayerOffsetData.setOffset(packet.playerUUID(), packet.offsetY());
                PacketDistributor.sendToAllPlayers(
                    new SyncOffsetBroadcastPacket(packet.playerUUID(), packet.offsetY())
                );
            }
        }).exceptionally(e -> {
            LOGGER.error("Failed to handle sync offset packet from client", e);
            return null;
        });
    }

    private static void handleSyncOffsetBroadcast(SyncOffsetBroadcastPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            PlayerOffsetData.setOffset(packet.playerUUID(), packet.offsetY());
        }).exceptionally(e -> {
            LOGGER.error("Failed to handle sync offset broadcast", e);
            return null;
        });
    }
}
