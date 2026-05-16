package com.github.zhan6ming.ultra_goggles.event;

import com.github.zhan6ming.ultra_goggles.item.UltraGogglesItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GogglesEventHandler {

    private static final int NIGHT_VISION_DURATION = 999999;

    private static final Set<UUID> FEATURE_ENABLED = ConcurrentHashMap.newKeySet();
    private static final Set<UUID> HAS_NIGHT_VISION = ConcurrentHashMap.newKeySet();
    public static final Set<UUID> HAS_LAVA_VISION = ConcurrentHashMap.newKeySet();

    public static void register() {
        NeoForge.EVENT_BUS.addListener(GogglesEventHandler::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(GogglesEventHandler::onEquipmentChange);
    }

    public static void toggleFeature(Player player) {
        UUID uuid = player.getUUID();
        if (FEATURE_ENABLED.contains(uuid)) {
            FEATURE_ENABLED.remove(uuid);
            clearEffects(player);
        } else {
            FEATURE_ENABLED.add(uuid);
        }
    }

    public static boolean hasLavaVision(Player player) {
        return HAS_LAVA_VISION.contains(player.getUUID());
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        @SuppressWarnings("resource")
        Level level = player.level();
        if (level.isClientSide()) return;

        UUID uuid = player.getUUID();
        boolean wearing = isWearingUltraGoggles(player);
        boolean featureOn = FEATURE_ENABLED.contains(uuid);

        if (wearing && featureOn) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, NIGHT_VISION_DURATION, 0, false, false, true));
            HAS_NIGHT_VISION.add(uuid);
            HAS_LAVA_VISION.add(uuid);
        } else {
            clearEffects(player);
        }
    }

    public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getSlot() != EquipmentSlot.HEAD) return;

        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();

        if (from.getItem() instanceof UltraGogglesItem && !(to.getItem() instanceof UltraGogglesItem)) {
            clearEffects(player);
            FEATURE_ENABLED.remove(player.getUUID());
        }
    }

    private static boolean isWearingUltraGoggles(Player player) {
        if (player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof UltraGogglesItem) {
            return true;
        }
        if (net.neoforged.fml.ModList.get().isLoaded("curios")) {
            return isWearingInCurios(player);
        }
        return false;
    }

    private static boolean isWearingInCurios(Player player) {
        return com.github.zhan6ming.ultra_goggles.compat.curios.CuriosCompat.resolveCuriosMap(player)
            .map(curiosMap -> {
                for (var stacksHandler : curiosMap.values()) {
                    int slots = stacksHandler.getSlots();
                    for (int slot = 0; slot < slots; slot++) {
                        if (stacksHandler.getStacks().getStackInSlot(slot).getItem() instanceof UltraGogglesItem) {
                            return true;
                        }
                    }
                }
                return false;
            })
            .orElse(false);
    }

    private static void clearEffects(Player player) {
        UUID uuid = player.getUUID();
        if (HAS_NIGHT_VISION.remove(uuid)) {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }
        HAS_LAVA_VISION.remove(uuid);
    }
}
