package net.tyzo.piratesbounty.entity.custom;

import mod.azure.azurelib.core.utils.MathUtils;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tyzo.piratesbounty.entity.ModEntities;

public class MusketBallEntity extends PersistentProjectileEntity {
	private static final TrackedData<Boolean> HIT =
			DataTracker.registerData(MusketBallEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int counter = 0;

	public MusketBallEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
		super(entityType, world);
	}

	public MusketBallEntity(World world, double x, double y, double z) {
		super(ModEntities.MUSKET_BALL_PROJECTILE, x, y, z, world);
	}

	public MusketBallEntity(World world, LivingEntity owner) {
		super(ModEntities.MUSKET_BALL_PROJECTILE, owner, world);

		setOwner(owner);
		BlockPos blockpos = owner.getBlockPos();

		double d0 = (double)blockpos.getX() + 0.5D;
		double d1 = (double)blockpos.getY() + 2.0D;
		double d2 = (double)blockpos.getZ() + 0.5D;

		this.refreshPositionAndAngles(d0, d1, d2, this.getYaw(), this.getPitch());
	}

	@Override
	public void tick() {
		super.tick();
		if(this.inGround) {
			this.discard();
		}

		if(this.dataTracker.get(HIT)) {
			if(this.age >= counter) {
				this.discard();
			}
		}

		if (this.age >= 300) {
			this.remove(RemovalReason.DISCARDED);
		}

		Vec3d vec3 = this.getVelocity();
		HitResult hitresult = ProjectileUtil.getCollision(this, this::canHit);
		if (hitresult.getType() != HitResult.Type.MISS)
			this.onCollision(hitresult);

		double d0 = this.getX() + vec3.x;
		double d1 = this.getY() + vec3.y;
		double d2 = this.getZ() + vec3.z;
		this.updateRotation();

		if (this.getWorld().getStatesInBox(this.getBoundingBox()).noneMatch(AbstractBlock.AbstractBlockState::isAir)) {
			this.discard();
		} else if (this.isInsideWaterOrBubbleColumn()) {
			this.discard();
		} else {
			this.setVelocity(vec3.multiply(1.0F));
			this.setPos(d0, d1, d2);
		}
	}

//	@Override
//	protected void onEntityHit(EntityHitResult entityHitResult) {
//		Entity hitEntity = entityHitResult.getEntity();
//		Entity owner = this.getOwner();
//
//		if(hitEntity == owner && this.getWorld().isClient()) {
//			return;
//		}
//
//		LivingEntity livingentity = owner instanceof LivingEntity ? (LivingEntity)owner : null;
//		float damage = 4f;
//		boolean hurt = hitEntity.damage(this.getDamageSources().mobProjectile(this, livingentity), damage);
//		if (hurt) {
//			if(hitEntity instanceof LivingEntity livingHitEntity) {
////				livingHitEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 1), owner);
//			}
//		}
//	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult)
	{
		Entity entity = entityHitResult.getEntity();
		if(entity.damage(entity.getDamageSources().arrow(this, this.getOwner() != null ? this.getOwner() : this), (float)this.getDamage()))
		{
			entity.timeUntilRegen = 0;
			if(entity instanceof EnderDragonPart) ((EnderDragonPart) entity).owner.timeUntilRegen = 0;
		}
		this.discard();
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult)
	{
		if (!this.getEntityWorld().isClient())
		{
			Block block = this.getWorld().getBlockState(blockHitResult.getBlockPos()).getBlock();
			if(block instanceof AbstractGlassBlock || block instanceof PaneBlock)
			{
				this.getWorld().breakBlock(blockHitResult.getBlockPos(), true, null, 512);
			}
			else
			{
//				float volumeAdjust = this.pelletGroupCount > 1 ? MathUtils.clamp((1.0f/(float)this.pelletGroupCount), 0.2f, 1.0f) : 1.0f;
				this.getEntityWorld().playSound(null, blockHitResult.getBlockPos(), this.getEntityWorld().getBlockState(blockHitResult.getBlockPos()).getSoundGroup().getBreakSound(), SoundCategory.BLOCKS);
			}
			((ServerWorld)this.getEntityWorld()).spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, this.getEntityWorld().getBlockState(blockHitResult.getBlockPos())), blockHitResult.getPos().getX(), blockHitResult.getPos().getY(), blockHitResult.getPos().getZ(), 5, 0.0, 0.0, 0.0, 0.5f);
		}
		BlockState blockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
		blockState.onProjectileHit(this.getWorld(), blockState, blockHitResult, this);

		this.discard();
	}

	@Override
	protected ItemStack asItemStack() {
		return ItemStack.EMPTY;
	}

//	@Override
//	protected void onBlockHit(BlockHitResult blockHitResult) {
//		BlockState blockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
//		blockState.onProjectileHit(this.getWorld(), blockState, blockHitResult, this);
//	}

//	@Override
//	protected void onCollision(HitResult hitResult) {
//		super.onCollision(hitResult);
//		if(this.getWorld().isClient()) {
//			return;
//		}
//
//		if(hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult entityHitResult) {
//			Entity hit = entityHitResult.getEntity();
//			Entity owner = this.getOwner();
//
//			if(owner != hit) {
//				this.dataTracker.set(HIT, true);
//				counter = this.age + 5;
//			}
//		} else if(hitResult.getType() == HitResult.Type.BLOCK) {
//			this.dataTracker.set(HIT, true);
//			counter = this.age + 5;
//		}
//	}

	@Override
	public boolean hasNoGravity() {
		return true;
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(HIT, false);
	}
}