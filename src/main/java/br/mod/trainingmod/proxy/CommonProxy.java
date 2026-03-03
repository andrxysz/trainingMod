package br.mod.trainingmod.proxy;

import br.mod.trainingmod.event.TrainingServerEvents;
import net.minecraftforge.common.MinecraftForge;

public class CommonProxy {

    public void preInit() {
    }

    public void init() {
        // eventos do forge
        TrainingServerEvents events = new TrainingServerEvents();
        MinecraftForge.EVENT_BUS.register(events);
    }
}

