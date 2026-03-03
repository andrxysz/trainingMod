package br.mod.trainingmod.proxy;

import br.mod.trainingmod.TrainingMod;
import br.mod.trainingmod.client.render.RenderTrainingNpc;
import br.mod.trainingmod.command.TrainingModCommand;
import br.mod.trainingmod.entity.EntityTrainingNpc;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit() {
        // entidade
        EntityRegistry.registerModEntity(
                EntityTrainingNpc.class,
                "training_npc",
                1,
                TrainingMod.INSTANCE,
                64,
                1,
                true
        );

        RenderingRegistry.registerEntityRenderingHandler(
                EntityTrainingNpc.class,
                new RenderTrainingNpc.Factory()
        );
    }

    @Override
    public void init() {
        super.init();

        ClientCommandHandler.instance.registerCommand(new TrainingModCommand());
    }
}

