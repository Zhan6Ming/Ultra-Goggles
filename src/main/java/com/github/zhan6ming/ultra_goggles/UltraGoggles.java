package com.github.zhan6ming.ultra_goggles;

import com.github.zhan6ming.ultra_goggles.item.UltraGogglesItem;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

@Mod(UltraGoggles.MODID)
public class UltraGoggles {

    public static final String MODID = "ultra_goggles";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<UltraGogglesItem> ULTRA_GOGGLES = ITEMS.register(
        "ultra_goggles",
        () -> new UltraGogglesItem(new Item.Properties())
    );

    public static final DeferredItem<Item> ROUGH_LENS = ITEMS.registerSimpleItem("rough_lens");

    public static final DeferredItem<Item> ULTRA_LENS = ITEMS.registerSimpleItem("ultra_lens");

    @SuppressWarnings("unused") // 注册副作用：类加载时触发 DeferredRegister 注册
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ULTRA_GOGGLES_TAB =
        CREATIVE_MODE_TABS.register("ultra_goggles_tab", () ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.ultra_goggles"))
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(() -> ULTRA_GOGGLES.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    output.accept(ULTRA_GOGGLES.get());
                    output.accept(ROUGH_LENS.get());
                    output.accept(ULTRA_LENS.get());
                })
                .build()
        );

    public UltraGoggles(IEventBus modEventBus, @SuppressWarnings("unused") ModContainer modContainer) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        modEventBus.addListener(this::commonSetup);

        com.github.zhan6ming.ultra_goggles.client.ClientSetup.register(modEventBus);
        com.github.zhan6ming.ultra_goggles.network.NetworkHandler.register(modEventBus);

        // GAME bus 事件处理器注册
        com.github.zhan6ming.ultra_goggles.event.GogglesEventHandler.register();
        if (net.neoforged.fml.loading.FMLEnvironment.dist == net.neoforged.api.distmarker.Dist.CLIENT) {
            com.github.zhan6ming.ultra_goggles.client.FogEventHandler.register();
            com.github.zhan6ming.ultra_goggles.client.KeyBindings.register();
        }

        if (ModList.get().isLoaded("curios")) {
            try {
                com.github.zhan6ming.ultra_goggles.compat.curios.CuriosCompat.init(modEventBus);
                LOGGER.debug("Curios compat enabled");
            } catch (Exception e) {
                LOGGER.warn("Curios compat failed: {}", e.getMessage());
            }
        }

        LOGGER.info("Ultra Goggles loaded!");
    }

    private void commonSetup(final net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent event) {
        com.simibubi.create.content.equipment.goggles.GogglesItem.addIsWearingPredicate(player ->
            player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).getItem() instanceof UltraGogglesItem
        );
        LOGGER.debug("Ultra Goggles common setup complete");
    }
}
