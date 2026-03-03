package br.mod.trainingmod.entity;

import br.mod.trainingmod.config.TrainingKnockbackMode;
import br.mod.trainingmod.entity.ai.EntityAITrainingMelee;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityTrainingNpc extends EntityMob {

    private static final String NBT_SKIN_UUID_MOST = "SkinUUIDMost";
    private static final String NBT_SKIN_UUID_LEAST = "SkinUUIDLeast";
    private static final String NBT_SKIN_NAME = "SkinName";
    private static final String NBT_OWNER_UUID_MOST = "OwnerUUIDMost";
    private static final String NBT_OWNER_UUID_LEAST = "OwnerUUIDLeast";
    private static final String NBT_OWNER_NAME = "OwnerName";

    private static final double HIT_SKIP_CHANCE = 0.08D;

    private GameProfile skinProfile;
    private ResourceLocation skinLocation;
    private UUID ownerUuid;
    private String ownerName;

    private double attackReach = 3.0D;
    private TrainingKnockbackMode knockbackMode = TrainingKnockbackMode.DEFAULT;

    public EntityTrainingNpc(World world) {
        super(world);
        this.experienceValue = 0;
        this.stepHeight = 1.0F;
        this.isImmuneToFire = false;
        setSize(0.6F, 1.8F);
        setupAi();
    }

    public EntityTrainingNpc(World world, GameProfile skinProfile) {
        this(world);
        setSkinProfile(skinProfile);
    }

    private void setupAi() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAITrainingMelee(this, 1.08D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25D);
        getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
        getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0D);
    }

    @Override
    public boolean canDespawn() {
        return false;
    }

    @Override
    public void onLivingUpdate() {
        if (!worldObj.isRemote) {
            EntityLivingBase currentTarget = getAttackTarget();
            if (currentTarget == null || currentTarget.isDead) {
                EntityPlayer target = findOwner();
                if (target == null) {
                    target = findNearestPlayer();
                }
                if (target != null) {
                    setAttackTarget(target);
                }
            }
        }
        super.onLivingUpdate();
    }

    public void onHitByPlayer(EntityPlayer player, float damage) {
        this.hurtResistantTime = 0;

        boolean hit = super.attackEntityFrom(DamageSource.causePlayerDamage(player), damage);
        if (!hit) {
            return;
        }

        int knockbackLevel = EnchantmentHelper.getKnockbackModifier(player);
        if (player.isSprinting()) {
            knockbackLevel++;
        }

        if (knockbackLevel > 0) {
            float yawRad = player.rotationYaw * (float) Math.PI / 180.0F;
            this.addVelocity(
                -MathHelper.sin(yawRad) * knockbackLevel * 0.5F,
                0.1D,
                MathHelper.cos(yawRad) * knockbackLevel * 0.5F
            );
            this.velocityChanged = true;
            player.motionX *= 0.6D;
            player.motionZ *= 0.6D;
            player.setSprinting(false);
        }

        setAttackTarget(player);
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (worldObj.isRemote) {
            return false;
        }

        boolean hit = super.attackEntityFrom(source, amount);
        if (!hit) {
            return false;
        }

        Entity src = source.getEntity();
        if (src instanceof EntityLivingBase) {
            setAttackTarget((EntityLivingBase) src);
        }

        this.setHealth(this.getMaxHealth());
        return true;
    }

    @Override
    public void knockBack(Entity attacker, float strength, double ratioX, double ratioZ) {
        if (worldObj.isRemote) {
            return;
        }
        super.knockBack(attacker, strength, ratioX, ratioZ);
        this.motionX *= knockbackMode.getHorizontalMultiplier();
        this.motionZ *= knockbackMode.getHorizontalMultiplier();
        this.motionY *= knockbackMode.getVerticalMultiplier();
        this.velocityChanged = true;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean attacked = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 1.0F);
        if (attacked) {
            entityIn.motionY += 0.02D;
        }
        return attacked;
    }

    @Override
    protected String getLivingSound() {
        return null;
    }

    @Override
    protected String getHurtSound() {
        return "game.player.hurt";
    }

    @Override
    protected String getDeathSound() {
        return "game.player.hurt";
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.hasKey(NBT_SKIN_UUID_MOST) && tag.hasKey(NBT_SKIN_UUID_LEAST)) {
            UUID uuid = new UUID(tag.getLong(NBT_SKIN_UUID_MOST), tag.getLong(NBT_SKIN_UUID_LEAST));
            setSkinProfile(new GameProfile(uuid, tag.getString(NBT_SKIN_NAME)));
        }
        if (tag.hasKey(NBT_OWNER_UUID_MOST) && tag.hasKey(NBT_OWNER_UUID_LEAST)) {
            this.ownerUuid = new UUID(tag.getLong(NBT_OWNER_UUID_MOST), tag.getLong(NBT_OWNER_UUID_LEAST));
            this.ownerName = tag.getString(NBT_OWNER_NAME);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        if (skinProfile != null && skinProfile.getId() != null) {
            tag.setLong(NBT_SKIN_UUID_MOST, skinProfile.getId().getMostSignificantBits());
            tag.setLong(NBT_SKIN_UUID_LEAST, skinProfile.getId().getLeastSignificantBits());
            tag.setString(NBT_SKIN_NAME, skinProfile.getName() == null ? "Player" : skinProfile.getName());
        }
        if (ownerUuid != null) {
            tag.setLong(NBT_OWNER_UUID_MOST, ownerUuid.getMostSignificantBits());
            tag.setLong(NBT_OWNER_UUID_LEAST, ownerUuid.getLeastSignificantBits());
            tag.setString(NBT_OWNER_NAME, ownerName == null ? "Player" : ownerName);
        }
    }

    public boolean shouldSkipHit() {
        return rand.nextDouble() < HIT_SKIP_CHANCE;
    }

    public double getAttackReach() {
        return attackReach;
    }

    public void setAttackReach(double attackReach) {
        this.attackReach = MathHelper.clamp_double(attackReach, 1.8D, 6.0D);
    }

    public TrainingKnockbackMode getKnockbackMode() {
        return knockbackMode;
    }

    public void setKnockbackMode(TrainingKnockbackMode mode) {
        this.knockbackMode = mode == null ? TrainingKnockbackMode.DEFAULT : mode;
    }

    public ResourceLocation getSkinLocation() {
        if (skinLocation != null) {
            return skinLocation;
        }
        if (skinProfile != null && skinProfile.getId() != null) {
            return DefaultPlayerSkin.getDefaultSkin(skinProfile.getId());
        }
        return DefaultPlayerSkin.getDefaultSkinLegacy();
    }

    public void setSkinProfile(GameProfile profile) {
        this.skinProfile = profile;
        resolveSkin();
    }

    public void setOwner(EntityPlayer owner) {
        if (owner == null || owner.getUniqueID() == null) {
            return;
        }
        this.ownerUuid = owner.getUniqueID();
        this.ownerName = owner.getName();
    }

    private EntityPlayer findOwner() {
        if (ownerUuid == null) {
            return null;
        }
        for (EntityPlayer p : worldObj.playerEntities) {
            if (p != null && ownerUuid.equals(p.getUniqueID()) && !p.isDead) {
                return p;
            }
        }
        return null;
    }

    private EntityPlayer findNearestPlayer() {
        EntityPlayer best = null;
        double bestDist = Double.MAX_VALUE;

        for (EntityPlayer p : worldObj.playerEntities) {
            if (p == null || p.isDead) {
                continue;
            }
            double d = getDistanceSqToEntity(p);
            if (d < bestDist) {
                bestDist = d;
                best = p;
            }
        }

        return best;
    }

    private void resolveSkin() {
        if (!worldObj.isRemote) {
            return;
        }
        if (skinProfile == null || skinProfile.getId() == null) {
            skinLocation = DefaultPlayerSkin.getDefaultSkinLegacy();
            return;
        }

        skinLocation = DefaultPlayerSkin.getDefaultSkin(skinProfile.getId());
        Minecraft.getMinecraft().getSkinManager().loadProfileTextures(
            skinProfile,
            new SkinManager.SkinAvailableCallback() {
                @Override
                public void skinAvailable(
                    MinecraftProfileTexture.Type type,
                    ResourceLocation location,
                    MinecraftProfileTexture profileTexture
                ) {
                    if (type == MinecraftProfileTexture.Type.SKIN) {
                        skinLocation = location;
                    }
                }
            },
            true
        );
    }
}
