package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.item.CompanionBatBundleItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CompanionBatPickUpItemGoal extends Goal {
    private final CompanionBatEntity entity;
    private LivingEntity owner;
    private final double speed;
    private final EntityNavigation navigation;
    private int updateCountdownTicks;
    private final float maxDistance;
    private ItemEntity targetItem;
    private ItemStack bundleStack;
    private List<ItemEntity> itemList;

    public CompanionBatPickUpItemGoal(CompanionBatEntity entity, double speed, float maxDistance) {
        this.entity = entity;
        this.speed = speed;
        this.navigation = entity.getNavigation();
        this.maxDistance = maxDistance;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
     }
  
    public boolean canStart() {
        this.bundleStack = this.entity.getBundle();
        if (this.bundleStack != null && this.bundleStack.isOf(Items.BUNDLE) && BundleItem.getAmountFilled(this.bundleStack) != 1){
            LivingEntity livingEntity = this.entity.getOwner();
            if (livingEntity == null) {
               return false;
            } else if (livingEntity.isSpectator()) {
               return false;
            } else {
               this.owner = livingEntity;
               this.itemList = this.entity.world.getEntitiesByClass(ItemEntity.class, this.entity.getBoundingBox().expand(8.0D, 8.0D, 8.0D), (itemEntity) -> true);
               return true && itemList.size() > 0;
            }
        }
        return false;
    }
  
    public boolean shouldContinue() {
        return this.targetItem != null && this.entity.squaredDistanceTo(this.owner) > (double)(this.maxDistance * this.maxDistance);
    }
  
    public void start() {
        this.updateCountdownTicks = 0;
        
        ItemEntity closestItem = null;
        double closestItemDistance = 9001;
        for (ItemEntity item : this.itemList){ 
            double distance = this.entity.squaredDistanceTo(item);
            CompanionBats.info(""+this.entity.squaredDistanceTo(item));
            if (distance < closestItemDistance){
                closestItem = item;
                closestItemDistance = distance; 
            }
        }
        this.itemList = null;
        this.targetItem = closestItem;
    }
  
    public void stop() {
        this.owner = null;
        this.navigation.stop();
    }

    public void tick() {
        this.entity.getLookControl().lookAt(this.owner, 10.0F, (float)this.entity.getLookPitchSpeed());
        if (--this.updateCountdownTicks <= 0) {
            this.updateCountdownTicks = 10;
            if (!this.entity.isLeashed() && !this.entity.hasVehicle() && this.targetItem != null) {
                if (this.entity.squaredDistanceTo(this.targetItem) < 2F) {
                    ItemStack targetStack = this.targetItem.getStack();
                    int added = ((CompanionBatBundleItem)new BundleItem(new Item.Settings())).addToBundlePublic(this.bundleStack, targetStack);
                    targetStack.decrement(added);
                    // TODO white smoke particles and soft shulker sound
                    this.targetItem = null;
                } else {
                    this.navigation.startMovingTo(this.targetItem, this.speed);
                }
            }
        }
    }
}
