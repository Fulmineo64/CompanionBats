package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

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
    private int canStartCountdownTicks;
    private final float maxDistance;
    private List<ItemEntity> itemList;
    private ItemEntity targetItem;
    private ItemStack bundleStack;

    public CompanionBatPickUpItemGoal(CompanionBatEntity entity, double speed, float maxDistance) {
        this.entity = entity;
        this.speed = speed;
        this.navigation = entity.getNavigation();
        this.maxDistance = maxDistance;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }
  
    public boolean canStart() {
        if (--this.canStartCountdownTicks <= 0) {
            // Makes sure this check isn't spammed
            this.canStartCountdownTicks = 10;
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
                    this.itemList.sort(new SortByProximity());
                    return this.itemList.size() > 0;
                }
            }
        }
        return false;
    }
  
    public boolean shouldContinue() {
        return this.targetItem != null && this.entity.squaredDistanceTo(this.owner) < (double)(this.maxDistance * this.maxDistance);
    }
  
    public void start() {
        this.targetNextItem();
    }

    public void stop() {
        this.itemList = null;
        this.owner = null;
        this.navigation.stop();
    }

    public void tick() {
        if (!this.entity.isLeashed() && !this.entity.hasVehicle() && this.targetItem != null) {
            if (this.entity.squaredDistanceTo(this.targetItem) < 2F) {
                ItemStack targetStack = this.targetItem.getStack();
                int added = ((CompanionBatBundleItem)new BundleItem(new Item.Settings())).companionBatsAddToBundle(this.bundleStack, targetStack);
                targetStack.decrement(added);
                // TODO white smoke particles and soft shulker sound
                this.removeItemFromList(this.targetItem);
                this.targetNextItem();
            } else {
                this.navigation.startMovingTo(this.targetItem, this.speed);
            }
        }
    }

    public void removeItemFromList(ItemEntity itemToRemove){
        for (Iterator<ItemEntity> iter = this.itemList.listIterator(); iter.hasNext(); ) {
            ItemEntity item = iter.next();
            if (item.equals(itemToRemove)) {
                iter.remove();
                return;
            }
        }
    }

    public void targetNextItem(){
        for (ItemEntity item : itemList){ 
            if (this.canItemFitInBundle(item.getStack())){
                this.targetItem = item;
                return;
            }
        }
        this.targetItem = null;
    }

    private boolean canItemFitInBundle(ItemStack itemStack) {
        return BundleItem.getAmountFilled(this.bundleStack) + (((CompanionBatBundleItem)new BundleItem(new Item.Settings())).companionBatsGetItemOccupancy(itemStack) / 64 * itemStack.getCount()) <= 1;
    }

    class SortByProximity implements Comparator<ItemEntity> {  
        // Used for sorting in ascending order of ID  
        public int compare(ItemEntity a, ItemEntity b) {  
            return (int)(CompanionBatPickUpItemGoal.this.entity.squaredDistanceTo(a) - CompanionBatPickUpItemGoal.this.entity.squaredDistanceTo(b));  
        }  
    }  
}
