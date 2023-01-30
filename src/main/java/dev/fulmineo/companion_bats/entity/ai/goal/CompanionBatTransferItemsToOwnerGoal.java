package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.Optional;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.mixin.BundleItemInvoker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class CompanionBatTransferItemsToOwnerGoal extends Goal {
    private final CompanionBatEntity entity;
    private PlayerEntity owner;
    private int canStartCountdownTicks;
    private final float maxDistance;
    private ItemStack bundleStack;
	private int updateCountdownTicks;
	private boolean canContinue = true;

    public CompanionBatTransferItemsToOwnerGoal(CompanionBatEntity entity, float maxDistance) {
        this.entity = entity;
        this.maxDistance = maxDistance;
    }

    public boolean canStart() {
        if (--this.canStartCountdownTicks <= 0) {
            // Makes sure this check isn't spammed
            this.canStartCountdownTicks = 10;
            if (this.entity.isRoosting() || this.entity.isAboutToRoost()) return false;
            this.bundleStack = this.entity.getBundle();
            if (!this.bundleStack.isOf(Items.BUNDLE)) return false;
            LivingEntity livingEntity = this.entity.getOwner();
            if (livingEntity == null) {
                return false;
            } else if (livingEntity.isSpectator()) {
                return false;
            } else if (this.entity.squaredDistanceTo(livingEntity) > (double)(this.maxDistance * this.maxDistance)) {
                return false;
            } else if (livingEntity instanceof PlayerEntity) {
                this.owner = (PlayerEntity)livingEntity;
                NbtCompound tag = this.bundleStack.getNbt();
                if (tag == null) return false;
				NbtList listTag = tag.getList("Items", 10);
				this.canContinue = listTag.size() > 0;
                return this.canContinue;
            }
        }
        return false;
    }

    public boolean shouldContinue() {
        return this.canContinue;
    }

    public void tick() {
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 5;
			this.canContinue = false;
            Optional<ItemStack> firstStack = BundleItemInvoker.invokeRemoveFirstStack(this.bundleStack);
			if (firstStack.isPresent()){
                if (owner.getInventory().insertStack(firstStack.get())){
					this.entity.world.playSound(null, this.entity.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.AMBIENT, 0.3F, 2F);
					this.canContinue = true;
                } else {
					BundleItemInvoker.invokeAddToBundle(this.bundleStack, firstStack.get());
                }
            }
        }
    }

    public void stop() {
        this.owner = null;
    }
}
