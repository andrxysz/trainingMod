package br.mod.trainingmod.client.render;

import br.mod.trainingmod.entity.EntityTrainingNpc;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderTrainingNpc extends RenderLiving<EntityTrainingNpc> {
    public RenderTrainingNpc(RenderManager renderManager) {
        super(renderManager, new ModelPlayer(0.0F, false), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTrainingNpc entity) {
        return entity.getSkinLocation();
    }

    public static class Factory implements IRenderFactory<EntityTrainingNpc> {
        @Override
        public RenderTrainingNpc createRenderFor(RenderManager manager) {
            return new RenderTrainingNpc(manager);
        }
    }
}

