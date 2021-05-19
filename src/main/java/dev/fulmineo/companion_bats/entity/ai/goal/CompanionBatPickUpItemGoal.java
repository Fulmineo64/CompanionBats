package dev.fulmineo.companion_bats.entity.ai.goal;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.item.CompanionBatPouchItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

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
            this.canStartCountdownTicks = 5;
            this.bundleStack = this.entity.getBundle();
            LivingEntity livingEntity = this.entity.getOwner();
            if (this.entity.isRoosting() || this.entity.isAboutToRoost()){
                return false;
            } else if (livingEntity == null || livingEntity.isSpectator()) {
                return false;
            } else if (this.isBundleAvailable()) {
                this.owner = livingEntity;
				if (this.owner.handSwinging) return false;
                return true;
            }
        }
        return false;
    }

    public boolean shouldContinue() {
        return this.targetItem != null && this.entity.squaredDistanceTo(this.owner) < (double)(this.maxDistance * this.maxDistance);
    }

    public void start() {
		this.itemList = this.entity.world.getEntitiesByClass(ItemEntity.class, this.entity.getBoundingBox().expand(12.0D, 12.0D, 12.0D), (itemEntity) -> itemEntity.getThrower() != this.owner.getUuid());
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
        if (!this.entity.isLeashed() && !this.entity.hasVehicle() && this.targetItem != null) {
            if (this.entity.squaredDistanceTo(this.targetItem) < 2F) {
				// Anti dupe measure (NOT needed in 1.17!)
				for (ItemEntity itemEntity: this.itemList){
					if (this.entity.squaredDistanceTo(this.targetItem) < 3F){
						itemEntity.resetPickupDelay();
					}
				}
				// Pick up the item
                ItemStack targetStack = this.targetItem.getStack();
                if (CompanionBatPouchItem.addItem(this.bundleStack, targetStack)){
					this.targetItem.getStack().decrement(targetStack.getCount());
				}
                this.entity.world.playSound(null, this.entity.getBlockPos(), SoundEvents.BLOCK_WOOL_PLACE, SoundCategory.AMBIENT, 0.3F, 1F);
                this.stop();
            } else {
				this.entity.scheduleTeleport(this.targetItem);
                this.navigation.startMovingTo(this.targetItem, this.speed);
            }
        }
    }

    private boolean isBundleAvailable(){
        return this.bundleStack != null && this.bundleStack.getItem() == CompanionBats.BAT_POUCH_ITEM && CompanionBatPouchItem.isEmpty(this.bundleStack);
    }

    class ProximityComparator implements Comparator<ItemEntity> {
        // Used for sorting in ascending order of ID
        public int compare(ItemEntity a, ItemEntity b) {
            return (int)(CompanionBatPickUpItemGoal.this.entity.squaredDistanceTo(a) - CompanionBatPickUpItemGoal.this.entity.squaredDistanceTo(b));
        }
    }
}
