package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.Optional;

import dev.fulmineo.companion_bats.CompanionBats;
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

public class CompanionBatTransferItemsToOwnerGoal extends Goal {
    private final CompanionBatEntity entity;
    private LivingEntity owner;
    private int canStartCountdownTicks;
    private final float maxDistance;
    private ItemStack bundleStack;

    public CompanionBatTransferItemsToOwnerGoal(CompanionBatEntity entity, float maxDistance) {
        this.entity = entity;
        this.maxDistance = maxDistance;
    }

    public boolean canStart() {
        if (--this.canStartCountdownTicks <= 0) {
            // Makes sure this check isn't spammed
            this.canStartCountdownTicks = 10;
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
                this.owner = livingEntity;
                CompoundTag tag = this.bundleStack.getTag();
                if (tag == null) return false;
                ListTag listTag = tag.getList("Items", 10);
                return listTag.size() > 0;
            }
        }
        return false;
    }

    public boolean shouldContinue() {
        return false;
    }
  
    public void start() {
        PlayerEntity owner = (PlayerEntity)this.owner;
        Optional<ItemStack> firstStack = ((CompanionBatBundleItem)new BundleItem(new Item.Settings())).companionBatsGetFirstStack(this.bundleStack);
        if (firstStack.isPresent()){
            if (!owner.getInventory().insertStack(firstStack.get())){
                ((CompanionBatBundleItem)new BundleItem(new Item.Settings())).companionBatsAddToBundle(this.bundleStack, firstStack.get());
            }
        }
    }
  
    public void stop() {
        this.owner = null;
    }
}
