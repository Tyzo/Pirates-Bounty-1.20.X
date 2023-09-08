package net.tyzo.piratesbounty.entity.custom;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
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

	public MusketBallEntity(World world, PlayerEntity player) {
		super(ModEntities.MUSKET_BALL_PROJECTILE, world);
		setOwner(player);
		BlockPos blockpos = player.getBlockPos();
		double d0 = (double)blockpos.getX() + 0.5D;
		double d1 = (double)blockpos.getY() + 1.75D;
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
			this.setVelocity(vec3.multiply(0.99F));
			this.setPos(d0, d1, d2);
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		Entity hitEntity = entityHitResult.getEntity();
		Entity owner = this.getOwner();

		if(hitEntity == owner && this.getWorld().isClient()) {
			return;
		}

		LivingEntity livingentity = owner instanceof LivingEntity ? (LivingEntity)owner : null;
		float damage = 2f;
		boolean hurt = hitEntity.damage(this.getDamageSources().mobProjectile(this, livingentity), damage);
		if (hurt) {
			if(hitEntity instanceof LivingEntity livingHitEntity) {
//				livingHitEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 1), owner);
			}
		}
	}

	@Override
	protected ItemStack asItemStack() {
		return ItemStack.EMPTY;
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		BlockState blockState = this.getWorld().getBlockState(blockHitResult.getBlockPos());
		blockState.onProjectileHit(this.getWorld(), blockState, blockHitResult, this);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if(this.getWorld().isClient()) {
			return;
		}

		if(hitResult.getType() == HitResult.Type.ENTITY && hitResult instanceof EntityHitResult entityHitResult) {
			Entity hit = entityHitResult.getEntity();
			Entity owner = this.getOwner();

			if(owner != hit) {
				this.dataTracker.set(HIT, true);
				counter = this.age + 5;
			}
		} else if(hitResult.getType() == HitResult.Type.BLOCK) {
			this.dataTracker.set(HIT, true);
			counter = this.age + 5;
		}
	}

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