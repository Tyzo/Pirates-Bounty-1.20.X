package net.tyzo.piratesbounty.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.tyzo.piratesbounty.entity.custom.MusketBallEntity;
import net.tyzo.piratesbounty.sound.ModSounds;

public class MusketItem extends Item {
	public MusketItem(Settings settings) {
		super(settings);
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		ItemStack itemstack = user.getStackInHand(hand);
		world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.SHOOT_1, SoundCategory.NEUTRAL, 1.0F, 1F);
		user.getItemCooldownManager().set(this, 0);

		if (!world.isClient()) {
			MusketBallEntity musketProjectile = new MusketBallEntity(world, user);
			musketProjectile.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 4.75F, 0.0F);
			world.spawnEntity(musketProjectile);
		}



		user.incrementStat(Stats.USED.getOrCreateStat(this));
		if (!user.getAbilities().creativeMode) {
			itemstack.damage(1, user, p -> p.sendToolBreakStatus(hand));
		}

		return super.use(world, user, hand);
	}
}
