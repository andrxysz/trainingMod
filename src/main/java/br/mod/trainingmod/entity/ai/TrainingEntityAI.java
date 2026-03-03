package br.mod.trainingmod.entity.ai;

import br.mod.trainingmod.entity.EntityTrainingNpc;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAITrainingMelee extends EntityAIBase {
    private final EntityTrainingNpc attacker;
    private final double moveSpeed;

    private int repathDelay;
    private int attackCooldown;

    public EntityAITrainingMelee(EntityTrainingNpc attacker, double moveSpeed) {
        this.attacker = attacker;
        this.moveSpeed = moveSpeed;
        setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        EntityLivingBase target = attacker.getAttackTarget();
        return target != null && target.isEntityAlive();
    }

    @Override
    public boolean continueExecuting() {
        EntityLivingBase target = attacker.getAttackTarget();
        return target != null && target.isEntityAlive();
    }

    @Override
    public void resetTask() {
        repathDelay = 0;
        attackCooldown = 0;
        attacker.getNavigator().clearPathEntity();
    }

    @Override
    public void updateTask() {
        EntityLivingBase target = attacker.getAttackTarget();
        if (target == null) {
            return;
        }

        attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);

        if (--repathDelay <= 0) {
            repathDelay = 4 + attacker.getRNG().nextInt(6);
            attacker.getNavigator().tryMoveToEntityLiving(target, moveSpeed);
        }

        if (attackCooldown > 0) {
            attackCooldown--;
        }

        double distanceSq = attacker.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
        double reachSq = attacker.getAttackReach() * attacker.getAttackReach() + target.width * target.width;
        if (distanceSq <= reachSq && attackCooldown <= 0) {
            attacker.swingItem();
            if (attacker.shouldSkipHit()) {
                attackCooldown = 8;
                return;
            }

            attackCooldown = 11;
            attacker.attackEntityAsMob(target);
        }
    }
}

