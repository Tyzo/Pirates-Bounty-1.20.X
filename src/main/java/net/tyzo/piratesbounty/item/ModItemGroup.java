package net.tyzo.piratesbounty.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.tyzo.piratesbounty.PiratesBountyMod;

public class ModItemGroup {
	public static final ItemGroup PIRATE_GROUP = Registry.register(Registries.ITEM_GROUP,
			new Identifier(PiratesBountyMod.MOD_ID, "pirate_group"),
			FabricItemGroup.builder().displayName(Text.translatable("itemgroup_pirate_group"))
					.icon(() -> new ItemStack(ModItems.FLINTLOCK_MUSKET)).entries((displayContext, entries) -> {
//						entries.add(ModItems.MUSKET_BALL);
//						entries.add(ModItems.MUSKET);
						entries.add(ModItems.FLINTLOCK_MUSKET);




					}).build());

	public static void registerItemGroups () {

	}
}
