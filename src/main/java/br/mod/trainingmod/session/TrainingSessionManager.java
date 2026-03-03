package br.mod.trainingmod.session;

import br.mod.trainingmod.config.TrainingDifficulty;
import br.mod.trainingmod.config.TrainingKnockbackMode;
import br.mod.trainingmod.entity.EntityTrainingNpc;
import com.mojang.authlib.GameProfile;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.concurrent.TimeUnit;

public final class TrainingSessionManager {
    private static final TrainingSessionManager INSTANCE = new TrainingSessionManager();

    private TrainingDifficulty difficulty = TrainingDifficulty.NORMAL;
    private TrainingKnockbackMode knockbackMode = TrainingKnockbackMode.DEFAULT;
    private EntityTrainingNpc activeNpc;

    private TrainingSessionManager() {
    }

    public static TrainingSessionManager getInstance() {
        return INSTANCE;
    }

    public TrainingDifficulty getDifficulty() {
        return difficulty;
    }

    public TrainingKnockbackMode getKnockbackMode() {
        return knockbackMode;
    }

    public EntityTrainingNpc spawnNpc(EntityPlayer player, TrainingDifficulty nextDifficulty) {
        if (player == null || player.worldObj == null) {
            return null;
        }

        this.difficulty = nextDifficulty == null ? TrainingDifficulty.NORMAL : nextDifficulty;
        clearInvalidNpc();
        if (activeNpc != null) {
            activeNpc.setDead();
        }

        if (player.worldObj.isRemote) {
            MinecraftServer server = MinecraftServer.getServer();
            if (server != null) {
                final WorldServer serverWorld = server.worldServerForDimension(player.dimension);
                final EntityPlayer serverPlayer = serverWorld == null ? null : serverWorld.getPlayerEntityByUUID(player.getUniqueID());
                if (serverWorld != null && serverPlayer != null) {
                    try {
                        final EntityTrainingNpc[] holder = new EntityTrainingNpc[1];
                        ListenableFuture<Object> future = server.addScheduledTask(new Runnable() {
                            @Override
                            public void run() {
                                holder[0] = createAndSpawnNpc(serverWorld, serverPlayer);
                            }
                        });
                        future.get(2L, TimeUnit.SECONDS);
                        EntityTrainingNpc spawnedNpc = holder[0];
                        if (spawnedNpc != null) {
                            activeNpc = spawnedNpc;
                            return spawnedNpc;
                        }
                    } catch (Exception ignored) {
                        // fallback
                    }
                }
            }
        } else {
            EntityTrainingNpc spawnedNpc = createAndSpawnNpc(player.worldObj, player);
            if (spawnedNpc != null) {
                activeNpc = spawnedNpc;
                return spawnedNpc;
            }
        }

        // Last fallback for client-only contexts where no integrated server player is available.
        EntityTrainingNpc fallbackNpc = createAndSpawnNpc(player.worldObj, player);
        if (fallbackNpc == null) {
            return null;
        }
        activeNpc = fallbackNpc;
        return fallbackNpc;
    }

    public void setKnockbackMode(TrainingKnockbackMode knockbackMode) {
        this.knockbackMode = knockbackMode == null ? TrainingKnockbackMode.DEFAULT : knockbackMode;
        clearInvalidNpc();
        if (activeNpc != null) {
            activeNpc.setKnockbackMode(this.knockbackMode);
        }
    }

    public EntityTrainingNpc getActiveNpc() {
        clearInvalidNpc();
        return activeNpc;
    }

    private void clearInvalidNpc() {
        if (activeNpc == null) {
            return;
        }
        if (activeNpc.isDead || activeNpc.worldObj == null) {
            activeNpc = null;
        }
    }

    private EntityTrainingNpc createAndSpawnNpc(World world, EntityPlayer owner) {
        if (world == null || owner == null) {
            return null;
        }

        GameProfile profile = owner.getGameProfile();
        EntityTrainingNpc npc = new EntityTrainingNpc(world, profile);

        Vec3 lookVec = owner.getLookVec();
        if (lookVec == null) {
            lookVec = new Vec3(0.0D, 0.0D, 1.0D);
        }
        double spawnDistance = 2.0D;
        double spawnX = owner.posX - lookVec.xCoord * spawnDistance;
        double spawnY = owner.posY;
        double spawnZ = owner.posZ - lookVec.zCoord * spawnDistance;

        npc.setPositionAndRotation(spawnX, spawnY, spawnZ, owner.rotationYaw + 180.0F, 0.0F);
        npc.setAttackReach(this.difficulty.getHitReach());
        npc.setKnockbackMode(this.knockbackMode);
        npc.setOwner(owner);
        npc.setHealth(npc.getMaxHealth());
        npc.setAttackTarget(owner);

        return world.spawnEntityInWorld(npc) ? npc : null;
    }
}

