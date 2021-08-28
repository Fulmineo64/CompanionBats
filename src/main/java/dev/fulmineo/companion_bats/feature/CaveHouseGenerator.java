package dev.fulmineo.companion_bats.feature;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;

import dev.fulmineo.companion_bats.CompanionBats;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.rcon.IServer;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.jigsaw.FeatureJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.SingleJigsawPiece;
import net.minecraft.world.gen.feature.structure.IglooPieces;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.server.ServerWorld;

public class CaveHouseGenerator {
	static final ResourceLocation TOP_TEMPLATE = new ResourceLocation("companion_bats","cave_house/top");
	private static final ResourceLocation MIDDLE_TEMPLATE = new ResourceLocation("companion_bats","cave_house/middle");
	private static final ResourceLocation BOTTOM_TEMPLATE = new ResourceLocation("companion_bats","cave_house/bottom");
	static final Map<ResourceLocation, BlockPos> OFFSETS;
	static final Map<ResourceLocation, BlockPos> OFFSETS_FROM_TOP;

	public static void addPieces(StructureManager manager, BlockPos pos, Rotation rotation, StructurePiecesHolder structurePiecesHolder, Random random) {
		if (random.nextDouble() < 0.5D) {
			int i = random.nextInt(4) + 4;
			structurePiecesHolder.addPiece(new CaveHouseGenerator.Piece(manager, BOTTOM_TEMPLATE, pos, rotation, i * 3));

			for(int j = 0; j < i - 1; ++j) {
				structurePiecesHolder.addPiece(new CaveHouseGenerator.Piece(manager, MIDDLE_TEMPLATE, pos, rotation, j * 3));
			}
		}
		structurePiecesHolder.addPiece(new CaveHouseGenerator.Piece(manager, TOP_TEMPLATE, pos, rotation, 0));
	}

	static {
		OFFSETS = ImmutableMap.of(TOP_TEMPLATE, new BlockPos(3, 6, 3), MIDDLE_TEMPLATE, new BlockPos(1, 3, 1), BOTTOM_TEMPLATE, new BlockPos(5, 6, 3));
		OFFSETS_FROM_TOP = ImmutableMap.of(TOP_TEMPLATE, BlockPos.ZERO, MIDDLE_TEMPLATE, new BlockPos(7, -3, 3), BOTTOM_TEMPLATE, new BlockPos(1, -3, 1));
	}

	public static class Piece extends TemplateStructurePiece {
		public Piece(StructureManager manager, ResourceLocation identifier, BlockPos pos, Rotation rotation, int yOffset) {
			super(CompanionBats.CAVE_HOUSE_PIECE, 0, manager, identifier, identifier.toString(), createPlacementData(rotation, identifier), getPosOffset(identifier, pos, yOffset));
		}

		public Piece(ServerWorld world, CompoundNBT nbt) {
			super(CompanionBats.CAVE_HOUSE_PIECE, nbt, world, (identifier) -> {
				return createPlacementData(Rotation.valueOf(nbt.getString("Rot")), identifier);
			});
		}

		private static StructurePlacementData createPlacementData(Rotation rotation, ResourceLocation identifier) {
			return (new StructurePlacementData()).setRotation(rotation).setMirror(Mirror.NONE).setPosition((BlockPos)CaveHouseGenerator.OFFSETS.get(identifier)).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
		}

		private static BlockPos getPosOffset(ResourceLocation identifier, BlockPos pos, int yOffset) {
			BlockPos newPos = pos.offset((Vector3i)CaveHouseGenerator.OFFSETS_FROM_TOP.get(identifier)).below(yOffset);
			return newPos;
		}

		protected void addAdditionalSaveData(CompoundNBT nbt) {
			super.addAdditionalSaveData(nbt);
			nbt.putString("Rot", this.placeSettings.getRotation().name());
		}

		protected void handleDataMarker(String metadata, BlockPos pos, IServerWorld world, Random random, MutableBoundingBox boundingBox) {
			if ("chest".equals(metadata)) {
				world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				BlockEntity blockEntity = world.getBlockEntity(pos.below());
				if (blockEntity instanceof ChestBlockEntity) {
					((ChestBlockEntity)blockEntity).setLootTable(new ResourceLocation(CompanionBats.MOD_ID, "chests/cave_house_top"), random.nextLong());
				}
			} else if ("barrel".equals(metadata)) {
				world.setBlock(pos, Blocks.HAY_BLOCK.defaultBlockState(), 3);
				BlockEntity blockEntity = world.getBlockEntity(pos.below());
				if (blockEntity instanceof BarrelBlockEntity) {
					((BarrelBlockEntity)blockEntity).setLootTable(new ResourceLocation(CompanionBats.MOD_ID, "chests/cave_house_bottom"), random.nextLong());
				}
			}
		}

		public boolean generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
			ResourceLocation identifier = new ResourceLocation(this.identifier);
			StructurePlacementData structurePlacementData = createPlacementData(this.placeSettings.getRotation(), identifier);
			BlockPos blockPos = (BlockPos)CaveHouseGenerator.OFFSETS_FROM_TOP.get(identifier);
			BlockPos blockPos2 = this.pos.add(Structure.transform(structurePlacementData, new BlockPos(3 - blockPos.getX(), 0, -blockPos.getZ())));
			int i = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, blockPos2.getX(), blockPos2.getZ());
			BlockPos blockPos3 = this.pos;
			this.pos = this.pos.add(0, i - 50 - 25, 0);
			boolean bl = super.generate(world, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, pos);
			if (identifier.equals(CaveHouseGenerator.TOP_TEMPLATE)) {
				BlockPos blockPos4 = this.pos.add(Structure.transform(structurePlacementData, new BlockPos(8, 0, 4)));
				BlockState blockState = world.getBlockState(blockPos4.down());
				if (!blockState.isAir() && !blockState.isOf(Blocks.LADDER)) {
					world.setBlockState(blockPos4, Blocks.DEEPSLATE_TILES.getDefaultState(), Block.NOTIFY_ALL);
				}
			}

			this.pos = blockPos3;
			return bl;
		}
	}
}
