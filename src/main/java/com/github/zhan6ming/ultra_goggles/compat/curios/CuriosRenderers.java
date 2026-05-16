package com.github.zhan6ming.ultra_goggles.compat.curios;

import com.github.zhan6ming.ultra_goggles.UltraGoggles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@OnlyIn(Dist.CLIENT)
public class CuriosRenderers {

    public static final ModelLayerLocation LAYER = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(UltraGoggles.MODID, "ultra_goggles"), "main"
    );

    public static void register() {
        CuriosRendererRegistry.register(
            UltraGoggles.ULTRA_GOGGLES.get(),
            () -> new UltraGogglesCurioRenderer(
                Minecraft.getInstance().getEntityModels().bakeLayer(LAYER)
            )
        );
    }

    public static void onLayerRegister(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(LAYER, () ->
            LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0), 1, 1)
        );
    }
}
