package com.github.chainmailstudios.astromine.discoveries.common.world.generation.glacios;

import java.util.Random;

import com.github.chainmailstudios.astromine.common.noise.FastNoise;
import com.github.chainmailstudios.astromine.common.noise.OctaveNoiseSampler;
import com.github.chainmailstudios.astromine.common.noise.OpenSimplexNoise;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.StructuresConfig;

public class GlaciosChunkGenerator extends ChunkGenerator {
	public static Codec<GlaciosChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(Codec.LONG.fieldOf("seed").forGetter(gen -> gen.seed),
					RegistryLookupCodec.of(Registry.BIOME_KEY).forGetter(source -> source.biomeRegistry))
					.apply(instance, GlaciosChunkGenerator::new));
	private final long seed;
	private final Registry<Biome> biomeRegistry;

	private final OctaveNoiseSampler<OpenSimplexNoise> floorNoise;
	private final OctaveNoiseSampler<OpenSimplexNoise> ceilingNoise;
	private final FastNoise fastNoise;

	public GlaciosChunkGenerator(long seed, Registry<Biome> biomeRegistry) {
		super(new GlaciosBiomeSource(biomeRegistry, seed), new StructuresConfig(false));
		this.seed = seed;
		this.biomeRegistry = biomeRegistry;

		Random random = new Random();

		this.floorNoise = new OctaveNoiseSampler<>(OpenSimplexNoise.class, random, 3, 194.21, 12, 12);
		this.ceilingNoise = new OctaveNoiseSampler<>(OpenSimplexNoise.class, random, 2, 402.64, 4, 4);

		this.fastNoise = new FastNoise((int) seed);

		fastNoise.setCellularDistanceFunction(FastNoise.CellularDistanceFunction.EUCLIDEAN);
		fastNoise.setCellularReturnType(FastNoise.CellularReturnType.DISTANCE_TO_DIVISION);
		fastNoise.setFrequency(0.0125F);
	}

	@Override
	protected Codec<? extends ChunkGenerator> getCodec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long seed) {
		return withSeedCommon(seed);
	}

	public ChunkGenerator withSeedCommon(long seed) {
		return new GlaciosChunkGenerator(seed, biomeRegistry);
	}

	@Override
	public void buildSurface(ChunkRegion region, Chunk chunk) {

	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
		int x1 = chunk.getPos().getStartX();
		int z1 = chunk.getPos().getStartZ();

		int x2 = chunk.getPos().getEndX();
		int z2 = chunk.getPos().getEndZ();

		ChunkRandom chunkRandom = new ChunkRandom();
		chunkRandom.setTerrainSeed(chunk.getPos().x, chunk.getPos().z);

		BlockPos.Mutable mutable = new BlockPos.Mutable();
		mutable.setY(1);

		for (int x = x1; x <= x2; ++x) {
			mutable.setX(x);
			for (int z = z1; z <= z2; ++z) {
				mutable.setZ(z);
				int floorExtent = (int) (30 + floorNoise.sample(x, z));
				int ceilingStart = (int) (200 + ceilingNoise.sample(x, z));
				int ceilingEnd = ceilingStart + 5;

				for (int y = 0; y < ceilingEnd; y++) {
					mutable.setY(y);
					if (y <= floorExtent) { // Packed ice floor
						world.setBlockState(mutable, Blocks.PACKED_ICE.getDefaultState(), 3);
					} else if (y >= ceilingStart) { // Ice roof
						world.setBlockState(mutable, Blocks.ICE.getDefaultState(), 3);
					} else {
						double voronoiAt = fastNoise.getCellular(x, y, z);

						if (voronoiAt > -0.125) { // Ice spires
							world.setBlockState(mutable, Blocks.ICE.getDefaultState(), 3);
						}
					}

					// Bedrock
					if (y <= 5) {
						if (chunkRandom.nextInt(y + 1) == 0) {
							chunk.setBlockState(mutable, Blocks.BEDROCK.getDefaultState(), false);
						}
					}
				}
			}
		}
	}

	@Override
	public int getHeight(int x, int z, Heightmap.Type heightmapType) {
		return 0;
	}

	@Override
	public BlockView getColumnSample(int x, int z) {
		return null;
	}
}