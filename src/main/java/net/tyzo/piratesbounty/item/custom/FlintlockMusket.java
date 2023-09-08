package net.tyzo.piratesbounty.item.custom;

import com.google.common.collect.Lists;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.tyzo.piratesbounty.entity.custom.MusketBallEntity;
import net.tyzo.piratesbounty.item.ModItems;
import net.tyzo.piratesbounty.sound.ModSounds;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FlintlockMusket extends RangedWeaponItem implements Vanishable {
		private static final String CHARGED_KEY = "Charged";
		private static final String CHARGED_PROJECTILES_KEY = "ChargedProjectiles";
		private boolean charged = false;
		private boolean loaded = false;
		
	public FlintlockMusket(Settings settings) {
		super(settings);
	}

	@Override
	public Predicate<ItemStack> getHeldProjectiles() {
		return CROSSBOW_HELD_PROJECTILES;
	}

	@Override
	public Predicate<ItemStack> getProjectiles() {
		return BOW_PROJECTILES;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemStack = user.getStackInHand(hand);

		if (FlintlockMusket.isCharged(itemStack)) {
			world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.SHOOT_1, SoundCategory.NEUTRAL, 1.0F, 1F);

			if (!world.isClient()) {
				MusketBallEntity musketProjectile = new MusketBallEntity(world, user);
				musketProjectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 4.75F, 0.0F);
				world.spawnEntity(musketProjectile);
			}

			FlintlockMusket.setCharged(itemStack, false);
			return TypedActionResult.consume(itemStack);
		}
		if (!user.getProjectileType(itemStack).isEmpty()) {
			if (!FlintlockMusket.isCharged(itemStack)) {
				this.charged = false;
				this.loaded = false;
				user.setCurrentHand(hand);
			}
			return TypedActionResult.consume(itemStack);
		}
		return TypedActionResult.fail(itemStack);
	}

	@Override
	public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		int i = this.getMaxUseTime(stack) - remainingUseTicks;
		float f = FlintlockMusket.getPullProgress(i, stack);
		if (f >= 1.0f && !FlintlockMusket.isCharged(stack) && FlintlockMusket.loadProjectiles(user, stack)) {
			FlintlockMusket.setCharged(stack, true);
			SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
			world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.MUSKET_LOADING_END, soundCategory, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.5f + 1.0f) + 0.2f);
		}
	}

	private static boolean loadProjectiles(LivingEntity shooter, ItemStack musket) {
		int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, musket);
		int j = i == 0 ? 1 : 3;
		boolean bl = shooter instanceof PlayerEntity && ((PlayerEntity)shooter).getAbilities().creativeMode;
		ItemStack itemStack = shooter.getProjectileType(musket);
		ItemStack itemStack2 = itemStack.copy();
		for (int k = 0; k < j; ++k) {
			if (k > 0) {
				itemStack = itemStack2.copy();
			}
			if (itemStack.isEmpty() && bl) {
				itemStack = new ItemStack(ModItems.MUSKET_BALL);
				itemStack2 = itemStack.copy();
			}
			if (FlintlockMusket.loadProjectile(shooter, musket, itemStack, k > 0, bl)) continue;
			return false;
		}
		return true;
	}

	private static boolean loadProjectile(LivingEntity shooter, ItemStack musket, ItemStack projectile, boolean simulated, boolean creative) {
		ItemStack itemStack;
		boolean bl;
		if (projectile.isEmpty()) {
			return false;
		}
		boolean bl2 = bl = creative && projectile.getItem() instanceof ArrowItem;
		if (!(bl || creative || simulated)) {
			itemStack = projectile.split(1);
			if (projectile.isEmpty() && shooter instanceof PlayerEntity) {
				((PlayerEntity)shooter).getInventory().removeOne(projectile);
			}
		} else {
			itemStack = projectile.copy();
		}
		FlintlockMusket.putProjectile(musket, itemStack);
		return true;
	}

	public static boolean isCharged(ItemStack stack) {
		NbtCompound nbtCompound = stack.getNbt();
		return nbtCompound != null && nbtCompound.getBoolean(CHARGED_KEY);
	}

	public static void setCharged(ItemStack stack, boolean charged) {
		NbtCompound nbtCompound = stack.getOrCreateNbt();
		nbtCompound.putBoolean(CHARGED_KEY, charged);
	}

	private static void putProjectile(ItemStack musket, ItemStack projectile) {
		NbtCompound nbtCompound = musket.getOrCreateNbt();
		NbtList nbtList = nbtCompound.contains(CHARGED_PROJECTILES_KEY, NbtElement.LIST_TYPE) ? nbtCompound.getList(CHARGED_PROJECTILES_KEY, NbtElement.COMPOUND_TYPE) : new NbtList();
		NbtCompound nbtCompound2 = new NbtCompound();
		projectile.writeNbt(nbtCompound2);
		nbtList.add(nbtCompound2);
		nbtCompound.put(CHARGED_PROJECTILES_KEY, nbtList);
	}

	private static List<ItemStack> getProjectiles(ItemStack musket) {
		NbtList nbtList;
		ArrayList<ItemStack> list = Lists.newArrayList();
		NbtCompound nbtCompound = musket.getNbt();
		if (nbtCompound != null && nbtCompound.contains(CHARGED_PROJECTILES_KEY, NbtElement.LIST_TYPE) && (nbtList = nbtCompound.getList(CHARGED_PROJECTILES_KEY, NbtElement.COMPOUND_TYPE)) != null) {
			for (int i = 0; i < nbtList.size(); ++i) {
				NbtCompound nbtCompound2 = nbtList.getCompound(i);
				list.add(ItemStack.fromNbt(nbtCompound2));
			}
		}
		return list;
	}

	private static void clearProjectiles(ItemStack musket) {
		NbtCompound nbtCompound = musket.getNbt();
		if (nbtCompound != null) {
			NbtList nbtList = nbtCompound.getList(CHARGED_PROJECTILES_KEY, NbtElement.LIST_TYPE);
			nbtList.clear();
			nbtCompound.put(CHARGED_PROJECTILES_KEY, nbtList);
		}
	}

	public static boolean hasProjectile(ItemStack musket, Item projectile) {
		return FlintlockMusket.getProjectiles(musket).stream().anyMatch(s -> s.isOf(projectile));
	}

	private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack musket, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated) {
		ProjectileEntity projectileEntity;
		if (world.isClient) {
			return;
		}
		boolean bl = projectile.isOf(Items.FIREWORK_ROCKET);
		if (bl) {
			projectileEntity = new FireworkRocketEntity(world, projectile, shooter, shooter.getX(), shooter.getEyeY() - (double)0.15f, shooter.getZ(), true);
		} else {
			projectileEntity = FlintlockMusket.createArrow(world, shooter, musket, projectile);
			if (creative || simulated != 0.0f) {
				((PersistentProjectileEntity)projectileEntity).pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
			}
		}
		if (shooter instanceof CrossbowUser) {
			CrossbowUser musketUser = (CrossbowUser) shooter;
			musketUser.shoot(musketUser.getTarget(), musket, projectileEntity, simulated);
		} else {
			Vec3d vec3d = shooter.getOppositeRotationVector(1.0f);
			Quaternionf quaternionf = new Quaternionf().setAngleAxis(simulated * ((float)Math.PI / 180), vec3d.x, vec3d.y, vec3d.z);
			Vec3d vec3d2 = shooter.getRotationVec(1.0f);
			Vector3f vector3f = vec3d2.toVector3f().rotate(quaternionf);
			projectileEntity.setVelocity(vector3f.x(), vector3f.y(), vector3f.z(), speed, divergence);
		}
		musket.damage(bl ? 3 : 1, shooter, e -> e.sendToolBreakStatus(hand));
		world.spawnEntity(projectileEntity);
		world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), ModSounds.SHOOT_2, SoundCategory.PLAYERS, 1.0f, soundPitch);
	}

	private static PersistentProjectileEntity createArrow(World world, LivingEntity entity, ItemStack musket, ItemStack arrow) {
		ArrowItem arrowItem = (ArrowItem)(arrow.getItem() instanceof ArrowItem ? arrow.getItem() : Items.ARROW);
		PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, arrow, entity);
		if (entity instanceof PlayerEntity) {
			persistentProjectileEntity.setCritical(true);
		}
		persistentProjectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
		persistentProjectileEntity.setShotFromCrossbow(true);
		int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, musket);
		if (i > 0) {
			persistentProjectileEntity.setPierceLevel((byte)i);
		}
		return persistentProjectileEntity;
	}

	public static void shootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence) {
		List<ItemStack> list = FlintlockMusket.getProjectiles(stack);
		float[] fs = FlintlockMusket.getSoundPitches(entity.getRandom());
		for (int i = 0; i < list.size(); ++i) {
			boolean bl;
			ItemStack itemStack = list.get(i);
			boolean bl2 = bl = entity instanceof PlayerEntity && ((PlayerEntity)entity).getAbilities().creativeMode;
			if (itemStack.isEmpty()) continue;
			if (i == 0) {
				FlintlockMusket.shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, 0.0f);
				continue;
			}
			if (i == 1) {
				FlintlockMusket.shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, -10.0f);
				continue;
			}
			if (i != 2) continue;
			FlintlockMusket.shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, 10.0f);
		}
		FlintlockMusket.postShoot(world, entity, stack);
	}

	private static float[] getSoundPitches(Random random) {
		boolean bl = random.nextBoolean();
		return new float[]{1.0f, FlintlockMusket.getSoundPitch(bl, random), FlintlockMusket.getSoundPitch(!bl, random)};
	}

	private static float getSoundPitch(boolean flag, Random random) {
		float f = flag ? 0.63f : 0.43f;
		return 1.0f / (random.nextFloat() * 0.5f + 1.8f) + f;
	}

	private static void postShoot(World world, LivingEntity entity, ItemStack stack) {
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
			if (!world.isClient) {
				Criteria.SHOT_CROSSBOW.trigger(serverPlayerEntity, stack);
			}
			serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
		}
		FlintlockMusket.clearProjectiles(stack);
	}

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		if (!world.isClient) {
			int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
			SoundEvent soundEvent = this.getQuickChargeSound(i);
			SoundEvent soundEvent2 = i == 0 ? ModSounds.MUSKET_LOADING_MIDDLE : null;
			float f = (float)(stack.getMaxUseTime() - remainingUseTicks) / (float)FlintlockMusket.getPullTime(stack);
			if (f < 0.2f) {
				this.charged = false;
				this.loaded = false;
			}
			if (f >= 0.2f && !this.charged) {
				this.charged = true;
				world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent, SoundCategory.PLAYERS, 0.5f, 1.0f);
			}
			if (f >= 0.5f && soundEvent2 != null && !this.loaded) {
				this.loaded = true;
				world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent2, SoundCategory.PLAYERS, 0.5f, 1.0f);
			}
		}
	}

	@Override
	public int getMaxUseTime(ItemStack stack) {
		return FlintlockMusket.getPullTime(stack) + 3;
	}

	public static int getPullTime(ItemStack stack) {
		int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
		return i == 0 ? 25 : 25 - 5 * i;
	}

	@Override
	public UseAction getUseAction(ItemStack stack) {
		return UseAction.CROSSBOW;
	}

	private SoundEvent getQuickChargeSound(int stage) {
		switch (stage) {
			case 1: {
				return ModSounds.MUSKET_QUICK_CHARGE_1;
			}
			case 2: {
				return ModSounds.MUSKET_QUICK_CHARGE_2;
			}
			case 3: {
				return ModSounds.MUSKET_QUICK_CHARGE_3;
			}
		}
		return ModSounds.MUSKET_LOADING_START;
	}

	private static float getPullProgress(int useTicks, ItemStack stack) {
		float f = (float)useTicks / (float)FlintlockMusket.getPullTime(stack);
		if (f > 1.0f) {
			f = 1.0f;
		}
		return f;
	}

	@Override
	public boolean isUsedOnRelease(ItemStack stack) {
		return stack.isOf(this);
	}

	@Override
	public int getRange() {
		return 32;
	}
}
