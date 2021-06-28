package dev.fulmineo.companion_bats.feature;

import com.mojang.serialization.Codec;

import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class CaveHouseFeature extends StructureFeature<DefaultFeatureConfig> {
	public CaveHouseFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
		return CaveHouseFeature.Start::new;
	}

	public static class Start extends StructureStart<DefaultFeatureConfig> {
		public Start(StructureFeature<DefaultFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
			super(structureFeature, chunkPos, i, l);
		}

		public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, ChunkPos chunkPos, Biome biome, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView) {
			BlockPos blockPos = new BlockPos(chunkPos.getStartX(), 50, chunkPos.getStartZ());
			// BlockRotation blockRotation = BlockRotation.random(this.random);
			BlockRotation blockRotation = BlockRotation.NONE;
			CaveHouseGenerator.addPieces(structureManager, blockPos, blockRotation, this, this.random);
		}
	}
}
