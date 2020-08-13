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

package com.github.chainmailstudios.astromine.common.world.generation.moon;

import com.github.chainmailstudios.astromine.common.world.layer.moon.MoonBiomeLayer;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.function.LongFunction;

public class MoonBiomeSource extends BiomeSource {
	public static Codec<MoonBiomeSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.LONG.fieldOf("seed").stable().forGetter(source -> source.seed),
			RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(source -> source.biomeRegistry)
	).apply(instance, instance.stable(MoonBiomeSource::new)));
	private final long seed;
	private final Registry<Biome> biomeRegistry;
	private final BiomeLayerSampler sampler;

	public MoonBiomeSource(long seed, Registry<Biome> biomeRegistry) {
		super(ImmutableList.of());
		this.seed = seed;
		this.biomeRegistry = biomeRegistry;
		this.sampler = build(seed);
	}

	@Override
	protected Codec<? extends BiomeSource> getCodec() {
		return CODEC;
	}

	@Override
	public BiomeSource withSeed(long seed) {
		return new MoonBiomeSource(seed, biomeRegistry);
	}

	@Override
	public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
		return sampler.sample(biomeRegistry, biomeX, biomeZ);
	}

	public BiomeLayerSampler build(long seed) {
		return new BiomeLayerSampler(build((salt) -> new CachingLayerContext(25, seed, salt)));
	}

	private <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(LongFunction<C> contextProvider) {
		LayerFactory<T> mainLayer = new MoonBiomeLayer(biomeRegistry).create(contextProvider.apply(4L));
		for (int i = 0; i < 5; i++) {
			mainLayer = ScaleLayer.FUZZY.create(contextProvider.apply(43 + i), mainLayer);
		}

		return mainLayer;
	}
}
