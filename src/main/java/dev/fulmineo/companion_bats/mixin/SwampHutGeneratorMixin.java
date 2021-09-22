package dev.fulmineo.companion_bats.mixin;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.fulmineo.companion_bats.CompanionBats;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePieceWithDimensions;
import net.minecraft.structure.SwampHutGenerator;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

@Mixin(SwampHutGenerator.class)
public class SwampHutGeneratorMixin extends StructurePieceWithDimensions {
	private boolean hasMainChest;

	public SwampHutGeneratorMixin(Random random, int i, int j) {
		super(StructurePieceType.SWAMP_HUT, i, 64, j, 7, 7, 9, getRandomHorizontalDirection(random));
	}

	@Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/nbt/NbtCompound;)V")
	public void swampHutGeneratorMixin(ServerWorld serverWorld, NbtCompound nbt, CallbackInfo ci) {
		this.hasMainChest = nbt.getBoolean("MainChest");
	}

	// Workaround to NOT override the generate method while keeping the StructurePieceWithDimensions extend
	@Shadow
	public boolean generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
		return false;
	}

	@Inject(at = @At("TAIL"), method = "generate(Lnet/minecraft/world/StructureWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z")
	public void generateMixin(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, ChunkPos chunkPos, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
		if (this.adjustToAverageHeight(world, boundingBox, 0)) {
			if (!this.hasMainChest) {
				this.hasMainChest = this.addChest(world, boundingBox, random, 2, 2, 6, new Identifier(CompanionBats.MOD_ID, "chests/swamp_hut"));
			}
		}
	}

	@Inject(at = @At("TAIL"), method = "writeNbt(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/nbt/NbtCompound;)V")
	protected void writeNbtMixin(ServerWorld world, NbtCompound nbt, CallbackInfo info) {
		nbt.putBoolean("MainChest", this.hasMainChest);
	}
}
