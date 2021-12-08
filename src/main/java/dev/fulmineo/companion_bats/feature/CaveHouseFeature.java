package dev.fulmineo.companion_bats.feature;

import com.mojang.serialization.Codec;

import net.minecraft.structure.StructureGeneratorFactory;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class CaveHouseFeature extends StructureFeature<DefaultFeatureConfig> {
	public CaveHouseFeature(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec, StructureGeneratorFactory.simple(StructureGeneratorFactory.checkForBiomeOnTop(Heightmap.Type.WORLD_SURFACE_WG), CaveHouseFeature::addPieces));
    }

    private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<DefaultFeatureConfig> context) {
		BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 50, context.chunkPos().getStartZ());
		// BlockRotation blockRotation = BlockRotation.random(this.random);
		BlockRotation blockRotation = BlockRotation.NONE;
		CaveHouseGenerator.addPieces(context.structureManager(), blockPos, blockRotation, collector, context.random());
    }
}
