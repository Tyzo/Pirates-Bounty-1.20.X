package net.tyzo.piratesbounty.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.tyzo.piratesbounty.PiratesBountyMod;

public class ModSounds {
	public static final SoundEvent SHOOT_1 = registerSoundEvent("shoot_1");
	public static final SoundEvent SHOOT_2 = registerSoundEvent("shoot_2");
	public static final SoundEvent SHOOT_3 = registerSoundEvent("shoot_3");
	public static final SoundEvent SHOOT_4 = registerSoundEvent("shoot_4");
	public static final SoundEvent MUSKET_QUICK_CHARGE_1 = registerSoundEvent("musket_quick_charge_1");
	public static final SoundEvent MUSKET_QUICK_CHARGE_2 = registerSoundEvent("musket_quick_charge_2");
	public static final SoundEvent MUSKET_QUICK_CHARGE_3 = registerSoundEvent("musket_quick_charge_3");
	public static final SoundEvent MUSKET_LOADING_START= registerSoundEvent("musket_loading_start");
	public static final SoundEvent MUSKET_LOADING_MIDDLE= registerSoundEvent("musket_loading_middle");
	public static final SoundEvent MUSKET_LOADING_END= registerSoundEvent("musket_loading_end");


	private static SoundEvent registerSoundEvent(String name) {
		Identifier identifier = new Identifier(PiratesBountyMod.MOD_ID, name);
		return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
	}

	public static void registerSounds() {
		PiratesBountyMod.LOGGER.info("Registering Mod Sounds for " + PiratesBountyMod.MOD_ID);
	}
}
