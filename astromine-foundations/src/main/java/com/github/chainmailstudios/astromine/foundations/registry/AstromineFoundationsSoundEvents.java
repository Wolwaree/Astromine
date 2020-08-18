package com.github.chainmailstudios.astromine.foundations.registry;

import com.github.chainmailstudios.astromine.foundations.AstromineFoundations;
import com.github.chainmailstudios.astromine.registry.AstromineSoundEvents;
import net.minecraft.sound.SoundEvent;

public class AstromineFoundationsSoundEvents {
	// Armory
	public static final SoundEvent COPPER_ARMOR_EQUIPPED = register("item.armor.equip_copper");
	public static final SoundEvent TIN_ARMOR_EQUIPPED = register("item.armor.equip_tin");
	public static final SoundEvent SILVER_ARMOR_EQUIPPED = register("item.armor.equip_silver");
	public static final SoundEvent LEAD_ARMOR_EQUIPPED = register("item.armor.equip_lead");

	public static final SoundEvent BRONZE_ARMOR_EQUIPPED = register("item.armor.equip_bronze");
	public static final SoundEvent STEEL_ARMOR_EQUIPPED = register("item.armor.equip_steel");
	public static final SoundEvent ELECTRUM_ARMOR_EQUIPPED = register("item.armor.equip_electrum");
	public static final SoundEvent ROSE_GOLD_ARMOR_EQUIPPED = register("item.armor.equip_rose_gold");
	public static final SoundEvent STERLING_SILVER_ARMOR_EQUIPPED = register("item.armor.equip_sterling_silver");
	public static final SoundEvent FOOLS_GOLD_ARMOR_EQUIPPED = register("item.armor.equip_fools_gold");

	public static final SoundEvent METITE_ARMOR_EQUIPPED = register("item.armor.equip_metite");
	public static final SoundEvent ASTERITE_ARMOR_EQUIPPED = register("item.armor.equip_asterite");
	public static final SoundEvent STELLUM_ARMOR_EQUIPPED = register("item.armor.equip_stellum");
	public static final SoundEvent GALAXIUM_ARMOR_EQUIPPED = register("item.armor.equip_galaxium");
	public static final SoundEvent UNIVITE_ARMOR_EQUIPPED = register("item.armor.equip_univite");
	public static final SoundEvent SPACE_SUIT_EQUIPPED = register("item.armor.equip_space_suit");

	public static SoundEvent register(String id) {
		return AstromineSoundEvents.register(AstromineFoundations.appendId(id));
	}
}
