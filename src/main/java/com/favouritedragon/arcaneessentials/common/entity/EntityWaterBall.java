package com.favouritedragon.arcaneessentials.common.entity;

import com.favouritedragon.arcaneessentials.common.util.ArcaneUtils;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

public class EntityWaterBall extends EntityMagicBolt {

	public EntityWaterBall(World world) {
		super(world);
	}

	private float damage = 5;

	public void setDamage(float damage) {
		this.damage = damage;
	}

	@Override
	protected boolean doGravity() {
		return true;
	}

	@Override
	public double getDamage() {
		return damage;
	}

	@Override
	public boolean doDeceleration() {
		return false;
	}

	@Override
	public int getLifetime() {
		return 200;
	}

	private void Splash() {
		if (!world.isRemote) {
			world.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_GENERIC_SPLASH, WizardrySounds.SPELLS, 1.0F + world.rand.nextFloat() / 10,
					0.8F + world.rand.nextFloat() / 10F);
			List<Entity> hit = world.getEntitiesWithinAABB(Entity.class, getEntityBoundingBox().grow(getSize() / 2));
			if (!hit.isEmpty()) {
				for (Entity target : hit) {
					if (target != this && target != getCaster()) {
						if (target.canBeCollidedWith()) {
							target.attackEntityFrom(MagicDamage.causeDirectMagicDamage(getCaster(),
									getDamageType()), (float) getDamage() * 0.4F);
							target.addVelocity(motionX / 4, motionY / 4, motionZ / 4);
							ArcaneUtils.applyPlayerKnockback(target);
						}
					}
				}
			}
			if (world instanceof WorldServer) {
				((WorldServer) world).spawnParticle(EnumParticleTypes.WATER_SPLASH, true, posX, posY, posZ, 30 + (int) (getSize() * 5),0, 0, 0, 0.01D);
			}
		}

		if (world.isRemote) {
			for (int i = 0; i < 20 - getSize(); i++) {
				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_BUBBLE).pos(getPositionVector()).time(10)
						.vel(world.rand.nextGaussian() / 10 * getSize(), world.rand.nextGaussian() / 10
								* getSize(), world.rand.nextGaussian() / 10 * getSize()).
						scale(0.25F + getSize() / 2 + world.rand.nextFloat()).spawn(world);
			}
		}
		this.isDead = true;
	}

	@Override
	protected void onEntityHit(EntityLivingBase entityHit) {
		super.onEntityHit(entityHit);
		if (entityHit != getCaster()) {
			setDead();
		}
	}

	@Override
	protected void onBlockHit(RayTraceResult hit) {
		setDead();
	}

	@Override
	protected void tickInGround() {
		setDead();
	}

	@Override
	public void setDead() {
		Splash();
	}
}