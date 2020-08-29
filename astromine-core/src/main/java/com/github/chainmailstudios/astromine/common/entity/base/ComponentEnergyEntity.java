package com.github.chainmailstudios.astromine.common.entity.base;

import com.github.chainmailstudios.astromine.common.component.inventory.EnergyInventoryComponent;
import com.github.chainmailstudios.astromine.common.utilities.capability.inventory.ExtendedInventoryProvider;
import com.github.chainmailstudios.astromine.registry.AstromineComponentTypes;
import nerdhub.cardinal.components.api.component.ComponentProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public abstract class ComponentEnergyEntity extends ComponentEntity {
	public abstract EnergyInventoryComponent createEnergyComponent();

	public EnergyInventoryComponent getEnergyComponent() {
		return ComponentProvider.fromEntity(this).getComponent(AstromineComponentTypes.ENERGY_INVENTORY_COMPONENT);
	}

	public ComponentEnergyEntity(EntityType<?> type, World world) {
		super(type, world);
	}
}
