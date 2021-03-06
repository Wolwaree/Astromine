/*
 * MIT License
 *
 * Copyright (c) 2020 Chainmail Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.chainmailstudios.astromine.technologies.common.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundTag;

import com.github.chainmailstudios.astromine.common.block.entity.base.ComponentEnergyFluidBlockEntity;
import com.github.chainmailstudios.astromine.common.component.inventory.EnergyInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.FluidInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleEnergyInventoryComponent;
import com.github.chainmailstudios.astromine.common.component.inventory.SimpleFluidInventoryComponent;
import com.github.chainmailstudios.astromine.common.utilities.tier.MachineTier;
import com.github.chainmailstudios.astromine.common.volume.energy.EnergyVolume;
import com.github.chainmailstudios.astromine.common.volume.fluid.FluidVolume;
import com.github.chainmailstudios.astromine.common.volume.fraction.Fraction;
import com.github.chainmailstudios.astromine.common.volume.handler.FluidHandler;
import com.github.chainmailstudios.astromine.registry.AstromineConfig;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.EnergySizeProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.FluidSizeProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.SpeedProvider;
import com.github.chainmailstudios.astromine.technologies.common.block.entity.machine.TierProvider;
import com.github.chainmailstudios.astromine.technologies.common.recipe.RefiningRecipe;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlockEntityTypes;
import com.github.chainmailstudios.astromine.technologies.registry.AstromineTechnologiesBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class RefineryBlockEntity extends ComponentEnergyFluidBlockEntity implements EnergySizeProvider, TierProvider, SpeedProvider, FluidSizeProvider {
	public double progress = 0;
	public int limit = 100;
	public boolean shouldTry = false;

	private Optional<RefiningRecipe> optionalRecipe = Optional.empty();

	public RefineryBlockEntity(Block energyBlock, BlockEntityType<?> type) {
		super(energyBlock, type);
	}

	@Override
	protected EnergyInventoryComponent createEnergyComponent() {
		return new SimpleEnergyInventoryComponent(getEnergySize());
	}

	@Override
	protected FluidInventoryComponent createFluidComponent() {
		FluidInventoryComponent fluidComponent = new SimpleFluidInventoryComponent(8).withInsertPredicate((direction, volume, slot) -> {
			if (slot != 0) {
				return false;
			}

			Fluid existing = this.fluidComponent.getVolume(0).getFluid();

			Fluid inserting = volume.getFluid();

			return RefiningRecipe.allows(world, inserting, existing);
		}).withExtractPredicate((direction, volume, slot) -> {
			return slot == 1 || slot == 2 || slot == 3 || slot == 4 || slot == 5 || slot == 6 || slot == 7;
		}).withListener((inventory) -> {
			shouldTry = true;
			optionalRecipe = Optional.empty();
		});

		FluidHandler.of(fluidComponent).getFirst().setSize(getFluidSize());
		FluidHandler.of(fluidComponent).getSecond().setSize(getFluidSize());
		FluidHandler.of(fluidComponent).getThird().setSize(getFluidSize());
		FluidHandler.of(fluidComponent).getFourth().setSize(getFluidSize());
		FluidHandler.of(fluidComponent).getFifth().setSize(getFluidSize());
		FluidHandler.of(fluidComponent).getSixth().setSize(getFluidSize());
		FluidHandler.of(fluidComponent).getSeventh().setSize(getFluidSize());
		FluidHandler.of(fluidComponent).getEighth().setSize(getFluidSize());

		return fluidComponent;
	}

	@Override
	public void tick() {
		super.tick();

		if (world == null)
			return;
		if (world.isClient)
			return;

		FluidHandler.ofOptional(this).ifPresent(fluids -> {
			EnergyVolume volume = getEnergyComponent().getVolume();
			if (!optionalRecipe.isPresent() && shouldTry) {
				optionalRecipe = (Optional) world.getRecipeManager().getAllOfType(RefiningRecipe.Type.INSTANCE).values().stream().filter(recipe -> recipe instanceof RefiningRecipe).filter(recipe -> ((RefiningRecipe) recipe).matches(fluidComponent)).findFirst();
				shouldTry = false;
			}

			if (optionalRecipe.isPresent()) {
				RefiningRecipe recipe = optionalRecipe.get();

				if (recipe.matches(fluidComponent)) {
					limit = recipe.getTime();

					double speed = Math.min(getMachineSpeed(), limit - progress);
					double consumed = recipe.getEnergyConsumed() * speed / limit;

					if (volume.hasStored(consumed)) {
						volume.minus(consumed);

						if (progress + speed >= limit) {
							optionalRecipe = Optional.empty();

							FluidVolume inputVolume = fluids.getFirst();
							FluidVolume firstOutputVolume = fluids.getSecond();
							FluidVolume secondOutputVolume = fluids.getThird();
							FluidVolume thirdOutputVolume = fluids.getFourth();
							FluidVolume fourthOutputVolume = fluids.getFifth();
							FluidVolume fifthOutputVolume = fluids.getSixth();
							FluidVolume sixthOutputVolume = fluids.getSeventh();
							FluidVolume seventhOutputVolume = fluids.getEighth();

							inputVolume.minus(recipe.getInputAmount());
							firstOutputVolume.moveFrom(FluidVolume.of(recipe.getFirstOutputAmount(), recipe.getFirstOutputFluid()), recipe.getFirstOutputAmount());
							secondOutputVolume.moveFrom(FluidVolume.of(recipe.getSecondOutputAmount(), recipe.getSecondOutputFluid()), recipe.getSecondOutputAmount());
							thirdOutputVolume.moveFrom(FluidVolume.of(recipe.getThirdOutputAmount(), recipe.getThirdOutputFluid()), recipe.getThirdOutputAmount());
							fourthOutputVolume.moveFrom(FluidVolume.of(recipe.getFourthOutputAmount(), recipe.getFourthOutputFluid()), recipe.getFourthOutputAmount());
							fifthOutputVolume.moveFrom(FluidVolume.of(recipe.getFifthOutputAmount(), recipe.getFifthOutputFluid()), recipe.getFifthOutputAmount());
							sixthOutputVolume.moveFrom(FluidVolume.of(recipe.getSixthOutputAmount(), recipe.getSixthOutputFluid()), recipe.getSixthOutputAmount());
							seventhOutputVolume.moveFrom(FluidVolume.of(recipe.getSeventhOutputAmount(), recipe.getSeventhOutputFluid()), recipe.getSeventhOutputAmount());

							progress = 0;
						} else {
							progress += speed;
						}

						tickActive();
					} else {
						tickInactive();
					}
				} else {
					tickInactive();
				}
			} else {
				tickInactive();
			}
		});
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		tag.putDouble("progress", progress);
		tag.putInt("limit", limit);
		return super.toTag(tag);
	}

	@Override
	public void fromTag(BlockState state, @NotNull CompoundTag tag) {
		progress = tag.getDouble("progress");
		limit = tag.getInt("limit");
		super.fromTag(state, tag);
	}

	public static class Primitive extends RefineryBlockEntity {
		public Primitive() {
			super(AstromineTechnologiesBlocks.PRIMITIVE_REFINERY, AstromineTechnologiesBlockEntityTypes.PRIMITIVE_REFINERY);
		}

		@Override
		public Fraction getFluidSize() {
			return Fraction.of(AstromineConfig.get().primitiveRefineryFluid, 1);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().primitiveRefinerySpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().primitiveRefineryEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.PRIMITIVE;
		}
	}

	public static class Basic extends RefineryBlockEntity {
		public Basic() {
			super(AstromineTechnologiesBlocks.BASIC_REFINERY, AstromineTechnologiesBlockEntityTypes.BASIC_REFINERY);
		}

		@Override
		public Fraction getFluidSize() {
			return Fraction.of(AstromineConfig.get().basicRefineryFluid, 1);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().basicRefinerySpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().basicRefineryEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.BASIC;
		}
	}

	public static class Advanced extends RefineryBlockEntity {
		public Advanced() {
			super(AstromineTechnologiesBlocks.ADVANCED_REFINERY, AstromineTechnologiesBlockEntityTypes.ADVANCED_REFINERY);
		}

		@Override
		public Fraction getFluidSize() {
			return Fraction.of(AstromineConfig.get().advancedRefineryFluid, 1);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().advancedRefinerySpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().advancedRefineryEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.ADVANCED;
		}
	}

	public static class Elite extends RefineryBlockEntity {
		public Elite() {
			super(AstromineTechnologiesBlocks.ELITE_REFINERY, AstromineTechnologiesBlockEntityTypes.ELITE_REFINERY);
		}

		@Override
		public Fraction getFluidSize() {
			return Fraction.of(AstromineConfig.get().eliteRefineryFluid, 1);
		}

		@Override
		public double getMachineSpeed() {
			return AstromineConfig.get().eliteRefinerySpeed;
		}

		@Override
		public double getEnergySize() {
			return AstromineConfig.get().eliteRefineryEnergy;
		}

		@Override
		public MachineTier getMachineTier() {
			return MachineTier.ELITE;
		}
	}
}
