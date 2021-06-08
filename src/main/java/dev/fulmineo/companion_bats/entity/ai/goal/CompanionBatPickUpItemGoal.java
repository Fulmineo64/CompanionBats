package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.item.CompanionBatPouchItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class CompanionBatPickUpItemGoal extends Goal {
    private final CompanionBatEntity entity;
    private LivingEntity owner;
    private final double speed;
    private final PathNavigator navigation;
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
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        if (--this.canStartCountdownTicks <= 0) {
            // Makes sure this check isn't spammed
            this.canStartCountdownTicks = 5;
            this.bundleStack = this.entity.getBundle();
            LivingEntity livingEntity = this.entity.getOwner();
            if (this.entity.isRoosting() || this.entity.isAboutToRoost()){
                return false;
            } else if (livingEntity == null || livingEntity.isSpectator()) {
                return false;
            } else if (this.isBundleAvailable()) {
                this.owner = livingEntity;
				if (this.owner.swinging) return false;
                return true;
            }
        }
        return false;
    }

    public boolean canContinueToUse() {
        return this.targetItem != null && this.entity.distanceToSqr(this.owner) < (double)(this.maxDistance * this.maxDistance);
    }

    public void start() {
		this.itemList = this.entity.level.getEntitiesOfClass(ItemEntity.class, this.entity.getBoundingBox().inflate(12.0D, 12.0D, 12.0D), (itemEntity) -> itemEntity.getThrower() != this.owner.getUUID());
		this.itemList.sort(new ProximityComparator());
		if (this.itemList.size() > 0){
			this.targetItem = this.itemList.get(0);
		}
    }

    public void stop() {
		this.targetItem = null;
		this.itemList = null;
        this.owner = null;
        this.navigation.stop();
    }

    public void tick() {
        if (!this.entity.isLeashed() && !this.entity.isPassenger() && this.targetItem != null) {
            if (this.entity.distanceToSqr(this.targetItem) < 2F) {
				// Anti dupe measure (NOT needed in 1.17!)
				for (ItemEntity itemEntity: this.itemList){
					if (this.entity.distanceToSqr(this.targetItem) < 3F){
						itemEntity.setDefaultPickUpDelay();
					}
				}
				// Pick up the item
                ItemStack targetStack = this.targetItem.getItem();
                if (CompanionBatPouchItem.addItem(this.bundleStack, targetStack)){
					this.targetItem.getItem().shrink(targetStack.getCount());
				}
                this.entity.level.playSound(null, this.entity.blockPosition(), SoundEvents.WOOL_PLACE, SoundCategory.AMBIENT, 0.3F, 1F);
                this.stop();
            } else {
                this.navigation.moveTo(this.targetItem, this.speed);
				this.navigation.getPath().setNextNodeIndex(1);
            }
        }
    }

    private boolean isBundleAvailable(){
        return this.bundleStack != null && this.bundleStack.getItem() == CompanionBats.BAT_POUCH_ITEM.get() && CompanionBatPouchItem.isEmpty(this.bundleStack);
    }

    class ProximityComparator implements Comparator<ItemEntity> {
        // Used for sorting in ascending order of ID
        public int compare(ItemEntity a, ItemEntity b) {
            return (int)(CompanionBatPickUpItemGoal.this.entity.distanceToSqr(a) - CompanionBatPickUpItemGoal.this.entity.distanceToSqr(b));
        }
    }
}
