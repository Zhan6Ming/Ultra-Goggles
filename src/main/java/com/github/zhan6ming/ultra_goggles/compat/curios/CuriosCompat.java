package com.github.zhan6ming.ultra_goggles.compat.curios;

import com.github.zhan6ming.ultra_goggles.item.UltraGogglesItem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Map;
import java.util.Optional;

public class CuriosCompat {

    public static void init(IEventBus modEventBus) {
        GogglesItem.addIsWearingPredicate(player -> resolveCuriosMap(player)
            .map(curiosMap -> {
                for (ICurioStacksHandler stacksHandler : curiosMap.values()) {
                    int slots = stacksHandler.getSlots();
                    for (int slot = 0; slot < slots; slot++) {
                        ItemStack stack = stacksHandler.getStacks().getStackInSlot(slot);
                        if (stack.getItem() instanceof UltraGogglesItem) {
                            return true;
                        }
                    }
                }
                return false;
            })
            .orElse(false));

        modEventBus.addListener(CuriosCompat::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        CuriosRenderers.register();
    }

    public static Optional<Map<String, ICurioStacksHandler>> resolveCuriosMap(LivingEntity entity) {
        return Optional.ofNullable(entity.getCapability(CuriosCapability.INVENTORY))
            .map(ICuriosItemHandler::getCurios);
    }
}
