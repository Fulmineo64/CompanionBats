package dev.fulmineo.companion_bats.mixin;

import java.util.Random;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.ScatteredStructurePiece;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.feature.structure.SwampHutPiece;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.fulmineo.companion_bats.CompanionBats;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;


@Mixin(SwampHutPiece.class)
public class SwampHutGeneratorMixin extends ScatteredStructurePiece {
	private boolean hasMainChest;

	public SwampHutGeneratorMixin(Random random, int i, int j) {
		super(IStructurePieceType.SWAMPLAND_HUT, random, i, 64, j, 7, 7, 9);
 	}

	@Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/world/gen/feature/template/TemplateManager;Lnet/minecraft/nbt/CompoundNBT;)V")
	public void swampHutGeneratorMixin(TemplateManager structureManager, CompoundNBT nbt, CallbackInfo ci) {
		this.hasMainChest = nbt.getBoolean("MainChest");
	}

	// Workaround to NOT override the generate method while keeping the StructurePieceWithDimensions extend
	@Shadow
	public boolean postProcess(ISeedReader world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos) {
		return false;
	}

	@Inject(at = @At("TAIL"), method = "postProcess(Lnet/minecraft/world/ISeedReader;Lnet/minecraft/world/gen/feature/structure/StructureManager;Lnet/minecraft/world/gen/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/MutableBoundingBox;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/util/math/BlockPos;)Z")
	public void postProcessMixin(ISeedReader world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, Random random, MutableBoundingBox boundingBox, ChunkPos chunkPos, BlockPos pos, CallbackInfoReturnable<Boolean> info) {
		if (this.updateAverageGroundHeight(world, boundingBox, 0)) {
			if (!this.hasMainChest) {
				this.hasMainChest = this.createChest(world, boundingBox, random, 2, 2, 6, new ResourceLocation(CompanionBats.MOD_ID, "chests/swamp_hut"));
			}
		}
	}

	@Inject(at = @At("TAIL"), method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundNBT;)V")
	protected void addAdditionalSaveDataMixin(CompoundNBT nbt, CallbackInfo info) {
		nbt.putBoolean("MainChest", this.hasMainChest);
	}
}