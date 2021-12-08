package dev.fulmineo.companion_bats.feature;

import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;

import dev.fulmineo.companion_bats.CompanionBats;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.SimpleStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiecesHolder;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class CaveHouseGenerator {
	static final Identifier TOP_TEMPLATE = new Identifier("companion_bats","cave_house/top");
	private static final Identifier MIDDLE_TEMPLATE = new Identifier("companion_bats","cave_house/middle");
	private static final Identifier BOTTOM_TEMPLATE = new Identifier("companion_bats","cave_house/bottom");
	static final Map<Identifier, BlockPos> OFFSETS;
	static final Map<Identifier, BlockPos> OFFSETS_FROM_TOP;

	public static void addPieces(StructureManager manager, BlockPos pos, BlockRotation rotation, StructurePiecesHolder structurePiecesHolder, Random random) {
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
		OFFSETS_FROM_TOP = ImmutableMap.of(TOP_TEMPLATE, BlockPos.ORIGIN, MIDDLE_TEMPLATE, new BlockPos(7, -3, 3), BOTTOM_TEMPLATE, new BlockPos(1, -3, 1));
	}

	public static class Piece extends SimpleStructurePiece {
		public Piece(StructureManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation, int yOffset) {
			super(CompanionBats.CAVE_HOUSE_PIECE, 0, manager, identifier, identifier.toString(), createPlacementData(rotation, identifier), getPosOffset(identifier, pos, yOffset));
		}

		public Piece(StructureContext context, NbtCompound nbt) {
			super(CompanionBats.CAVE_HOUSE_PIECE, nbt, context.structureManager(), (identifier) -> {
				return createPlacementData(BlockRotation.valueOf(nbt.getString("Rot")), identifier);
			});
		}

		private static StructurePlacementData createPlacementData(BlockRotation rotation, Identifier identifier) {
			return (new StructurePlacementData()).setRotation(rotation).setMirror(BlockMirror.NONE).setPosition((BlockPos)CaveHouseGenerator.OFFSETS.get(identifier)).addProcessor(BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
		}

		private static BlockPos getPosOffset(Identifier identifier, BlockPos pos, int yOffset) {
			BlockPos newPos = pos.add((Vec3i)CaveHouseGenerator.OFFSETS_FROM_TOP.get(identifier)).down(yOffset);
			return newPos;
		}

		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putString("Rot", this.placementData.getRotation().name());
		}

		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
			if ("chest".equals(metadata)) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
				BlockEntity blockEntity = world.getBlockEntity(pos.down());
				if (blockEntity instanceof ChestBlockEntity) {
					((ChestBlockEntity)blockEntity).setLootTable(new Identifier(CompanionBats.MOD_ID, "chests/cave_house_top"), random.nextLong());
				}
			} else if ("barrel".equals(metadata)) {
				world.setBlockState(pos, Blocks.HAY_BLOCK.getDefaultState(), Block.NOTIFY_ALL);
				BlockEntity blockEntity = world.getBlockEntity(pos.down());
				if (blockEntity instanceof BarrelBlockEntity) {
					((BarrelBlockEntity)blockEntity).setLootTable(new Identifier(CompanionBats.MOD_ID, "chests/cave_house_bottom"), random.nextLong());
				}
			}
		}

		public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
			Identifier identifier = new Identifier(this.template);
			StructurePlacementData structurePlacementData = createPlacementData(this.placementData.getRotation(), identifier);
			BlockPos blockPos = (BlockPos)CaveHouseGenerator.OFFSETS_FROM_TOP.get(identifier);
			BlockPos blockPos2 = this.pos.add(Structure.transform(structurePlacementData, new BlockPos(3 - blockPos.getX(), 0, -blockPos.getZ())));
			int i = world.getTopY(Heightmap.Type.WORLD_SURFACE_WG, blockPos2.getX(), blockPos2.getZ());
			BlockPos blockPos3 = this.pos;
			this.pos = this.pos.add(0, i - 50 - 25, 0);
			super.generate(world, structureAccessor, chunkGenerator, random, boundingBox, chunkPos, pos);
			if (identifier.equals(CaveHouseGenerator.TOP_TEMPLATE)) {
				/*
				// TODO: AzaleaSaplingGenerator softlocks the entire game
				BlockPos surfacePos = new BlockPos(pos.getX(), i, pos.getZ());
				BlockState surfaceState = world.getBlockState(surfacePos);
				if (surfaceState.isAir()) {
					world.setBlockState(surfacePos.down(), Blocks.GRASS.getDefaultState(), Block.NOTIFY_ALL);
					AzaleaSaplingGenerator gen = new AzaleaSaplingGenerator();
					gen.generate(world.toServerWorld(), world.toServerWorld().getChunkManager().getChunkGenerator(), pos, surfaceState, random);
				}
				*/

				BlockPos blockPos4 = this.pos.add(Structure.transform(structurePlacementData, new BlockPos(8, 0, 4)));
				BlockState blockState = world.getBlockState(blockPos4.down());
				if (!blockState.isAir() && !blockState.isOf(Blocks.LADDER)) {
					world.setBlockState(blockPos4, Blocks.DEEPSLATE_TILES.getDefaultState(), Block.NOTIFY_ALL);
				}
			}

			this.pos = blockPos3;
		}
	}
}
