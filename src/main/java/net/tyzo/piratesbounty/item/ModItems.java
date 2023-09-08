package net.tyzo.piratesbounty.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.tyzo.piratesbounty.PiratesBountyMod;
import net.tyzo.piratesbounty.item.custom.FlintlockMusket;

public class ModItems {
	public static final Item MUSKET_BALL = registerItem("musket_ball", new Item(new FabricItemSettings()));

	public static final Item FLINTLOCK_MUSKET = registerItem("flintlock_musket", new FlintlockMusket(new FabricItemSettings().maxDamage(500)));
	public static final Item PIRATE_HAT = registerItem("pirate_hat", new ArmorItem(ModArmorMaterials.LEATHER, ArmorItem.Type.HELMET, new FabricItemSettings().maxDamage(256)));

	public static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(PiratesBountyMod.MOD_ID, name), item);
	}

	public static void registerModItems() {
		PiratesBountyMod.LOGGER.info("Register Mod Items for " + PiratesBountyMod.MOD_ID);
	}

}
