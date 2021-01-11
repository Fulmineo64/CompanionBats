package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.Optional;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.item.CompanionBatBundleItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class CompanionBatTransferItemsToOwnerGoal extends Goal {
    private final CompanionBatEntity entity;
    private PlayerEntity owner;
    private int canStartCountdownTicks;
    private final float maxDistance;
    private ItemStack bundleStack;
    private ListTag listTag;
    private int updateCountdownTicks;

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
                CompoundTag tag = this.bundleStack.getTag();
                if (tag == null) return false;
                this.listTag = tag.getList("Items", 10);
                return this.listTag.size() > 0;
            }
        }
        return false;
    }

    public boolean shouldContinue() {
        return this.listTag != null && this.listTag.size() > 0;
    }

    public void tick() {
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 5;
            Optional<ItemStack> firstStack = ((CompanionBatBundleItem)new BundleItem(new Item.Settings())).companionBatsGetFirstStack(this.bundleStack);
            if (firstStack.isPresent()){
                if (owner.getInventory().insertStack(firstStack.get())){
                    this.entity.world.playSound(null, this.entity.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.AMBIENT, 0.3F, 2F);
                } else {
                    ((CompanionBatBundleItem)new BundleItem(new Item.Settings())).companionBatsAddToBundle(this.bundleStack, firstStack.get());
                }
            }
        }
    }
  
    public void stop() {
        this.owner = null;
    }
}
