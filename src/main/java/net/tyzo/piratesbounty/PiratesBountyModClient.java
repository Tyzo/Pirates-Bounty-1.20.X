package net.tyzo.piratesbounty;

import net.fabricmc.api.ClientModInitializer;
import net.tyzo.piratesbounty.util.ModModelPredicateProvider;

public class PiratesBountyModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModModelPredicateProvider.registerModModels();
	}
}
