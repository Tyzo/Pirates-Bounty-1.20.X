package net.tyzo.piratesbounty.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.tyzo.piratesbounty.PiratesBountyMod;
import net.tyzo.piratesbounty.entity.custom.MusketBallEntity;

public class ModEntities {
	public static final EntityType<MusketBallEntity> MUSKET_BALL_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
			new Identifier(PiratesBountyMod.MOD_ID, "musket_ball_projectile"),
			FabricEntityTypeBuilder.<MusketBallEntity>create(SpawnGroup.MISC, MusketBallEntity::new)
					.dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build());

	public static void registerModEntities() {
		PiratesBountyMod.LOGGER.info("Registering Mod Entities for " + PiratesBountyMod.MOD_ID);
	}
}
