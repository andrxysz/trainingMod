package br.mod.trainingmod.event;

import br.mod.trainingmod.entity.EntityTrainingNpc;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TrainingServerEvents {

    @SubscribeEvent
    public void onPlayerAttackNpc(AttackEntityEvent event) {
        if (event.entityPlayer == null || event.target == null) {
            return;
        }
        if (event.entityPlayer.worldObj.isRemote) {
            return;
        }
        if (!(event.target instanceof EntityTrainingNpc)) {
            return;
        }

        EntityTrainingNpc npc = (EntityTrainingNpc) event.target;
        EntityPlayer player = event.entityPlayer;
        if (npc.isDead || player.isDead) {
            return;
        }

        // A IA focará no alvo
        npc.setAttackTarget(player);
    }
}

