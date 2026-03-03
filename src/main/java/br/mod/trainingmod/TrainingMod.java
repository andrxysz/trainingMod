package br.mod.trainingmod;

import br.mod.trainingmod.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
        modid = TrainingMod.MOD_ID,
        name = TrainingMod.MOD_NAME,
        version = TrainingMod.VERSION,
        clientSideOnly = true,
        acceptedMinecraftVersions = "[1.8.9]"
)
public class TrainingMod {
    public static final String MOD_ID = "trainingmod";
    public static final String MOD_NAME = "Training Mod";
    public static final String VERSION = "1.0.0";

    @Mod.Instance(MOD_ID)
    public static TrainingMod INSTANCE;

    @SidedProxy(
            clientSide = "br.mod.trainingmod.proxy.ClientProxy",
            serverSide = "br.mod.trainingmod.proxy.CommonProxy"
    )
    private static CommonProxy proxy;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        proxy.preInit();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        proxy.init();
    }
}

