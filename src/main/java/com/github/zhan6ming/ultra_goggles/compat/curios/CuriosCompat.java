package com.github.zhan6ming.ultra_goggles.compat.curios;

import com.github.zhan6ming.ultra_goggles.item.UltraGogglesItem;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
        GogglesItem.addIsWearingPredicate(CuriosCompat::isWearingUltraGoggles);
        modEventBus.addListener(CuriosCompat::onClientSetup);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        CuriosRenderers.register();
    }

    /**
     * 解析玩家的 Curios 物品栏能力，返回 curiosMap。
     *
     * @param entity 要检查的实体
     * @return Curios 物品映射的 Optional 包装
     */
    public static Optional<Map<String, ICurioStacksHandler>> resolveCuriosMap(LivingEntity entity) {
        return Optional.ofNullable(entity.getCapability(CuriosCapability.INVENTORY))
            .map(ICuriosItemHandler::getCurios);
    }

    /**
     * 检查玩家是否穿戴了 Ultra Goggles（通过 Curios 槽位）。
     * 此方法同时用于 Create 的 GogglesItem 谓词和 GogglesEventHandler 的穿戴检测。
     *
     * @param player 要检查的玩家
     * @return 如果在 Curios 中找到 Ultra Goggles 返回 true
     */
    public static boolean isWearingUltraGoggles(Player player) {
        return resolveCuriosMap(player)
            .map(CuriosCompat::hasUltraGogglesCurio)
            .orElse(false);
    }

    /**
     * 遍历 Curios 物品映射，检查是否存在 Ultra Goggles。
     *
     * @param curiosMap Curios 物品映射
     * @return 如果找到 Ultra Goggles 返回 true
     */
    public static boolean hasUltraGogglesCurio(Map<String, ICurioStacksHandler> curiosMap) {
        if (curiosMap == null) return false;

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
    }
}
