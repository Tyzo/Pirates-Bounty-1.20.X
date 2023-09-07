package net.tyzo.piratesbounty.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.tyzo.piratesbounty.PiratesBountyMod;

public class ModItems {
//	public static final Item MUSKET_BALL = registerItem("musket_ball", new Item(new FabricItemSettings()));
//	public static final Item MUSKET = registerItem("musket", new CrossbowItem(new FabricItemSettings().maxDamage(1024)));

	public static final Item FLINTLOCK_MUSKET = registerItem("flintlock_musket", new CrossbowItem(new FabricItemSettings().maxDamage(500)));

	public static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(PiratesBountyMod.MOD_ID, name), item);
	}

	public static void registerModItems() {
		PiratesBountyMod.LOGGER.info("Register Mod Items for " + PiratesBountyMod.MOD_ID);
	}

}
