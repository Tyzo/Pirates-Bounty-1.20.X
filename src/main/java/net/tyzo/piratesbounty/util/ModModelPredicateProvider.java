package net.tyzo.piratesbounty.util;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.tyzo.piratesbounty.item.ModItems;

public class ModModelPredicateProvider {
	public static void registerModModels() {
		registerMusket(ModItems.FLINTLOCK_MUSKET);
//		registerMusket(ModItems.MUSKET);
	}

	private static void registerMusket(Item musket) {
		ModelPredicateProviderRegistry.register(musket, new Identifier("pull"), (stack, world, entity, seed) -> {
			if (entity == null) {
				return 0.0f;
			}
			if (CrossbowItem.isCharged(stack)) {
				return 0.0f;
			}
			return (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / (float) CrossbowItem.getPullTime(stack);
		});
		ModelPredicateProviderRegistry.register(musket, new Identifier("pulling"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack && !CrossbowItem.isCharged(stack) ? 1.0f : 0.0f);
		ModelPredicateProviderRegistry.register(musket, new Identifier("charged"), (stack, world, entity, seed) -> CrossbowItem.isCharged(stack) ? 1.0f : 0.0f);
		ModelPredicateProviderRegistry.register(musket, new Identifier("firework"), (stack, world, entity, seed) -> CrossbowItem.isCharged(stack) && CrossbowItem.hasProjectile(stack, Items.FIREWORK_ROCKET) ? 1.0f : 0.0f);
	}
}
