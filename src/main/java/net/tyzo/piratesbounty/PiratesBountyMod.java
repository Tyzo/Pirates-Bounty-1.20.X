package net.tyzo.piratesbounty;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.tyzo.piratesbounty.entity.ModEntities;
import net.tyzo.piratesbounty.entity.client.MusketBallProjectileModel;
import net.tyzo.piratesbounty.entity.client.MusketBallProjectileRender;
import net.tyzo.piratesbounty.entity.layer.ModModelLayers;
import net.tyzo.piratesbounty.item.ModItemGroup;
import net.tyzo.piratesbounty.item.ModItems;
import net.tyzo.piratesbounty.sound.ModSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PiratesBountyMod implements ModInitializer {
	public static String MOD_ID = "piratesbounty";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroup.registerItemGroups();
		ModItems.registerModItems();
		ModEntities.registerModEntities();
		ModSounds.registerSounds();

		EntityModelLayerRegistry.registerModelLayer(ModModelLayers.MUSKET_BALL_PROJECTILE, MusketBallProjectileModel::getTexturedModelData);

		EntityRendererRegistry.register(ModEntities.MUSKET_BALL_PROJECTILE, MusketBallProjectileRender::new);
	}
}