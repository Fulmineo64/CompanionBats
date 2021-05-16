package dev.fulmineo.companion_bats.entity.ai.goal;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import dev.fulmineo.companion_bats.CompanionBatAbility;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class CompanionBatThrowPotionGoal extends Goal {
    private final CompanionBatEntity entity;
    private LivingEntity owner;
	private final double maxDistanceSquared;
    private final PathNavigator navigation;
	private final int emergencyPotionCooldown;
	private final int effectPotionCooldown;
	private int updateCountdownTicks;
	private Integer effectPotionLevel;
	private Integer emergencyPotionLevel;

	public CompanionBatThrowPotionGoal(CompanionBatEntity entity, float maxDistance, int emergencyPotionCooldown, int effectPotionCooldown) {
		this.entity = entity;
        this.maxDistanceSquared = (double)(maxDistance * maxDistance);
		this.emergencyPotionCooldown = emergencyPotionCooldown;
		this.effectPotionCooldown = effectPotionCooldown;
		this.entity.emergencyPotionTicks = this.emergencyPotionCooldown;
		this.entity.effectPotionTicks = this.effectPotionCooldown;
        this.navigation = entity.getNavigation();
     }

    public boolean canUse() {
        LivingEntity livingEntity = this.entity.getOwner();
		if (livingEntity == null || livingEntity.isSpectator() || this.entity.isRoosting()) {
            return false;
        } else {
			if (this.entity.emergencyPotionTicks > 0) this.entity.emergencyPotionTicks--;
			if (this.entity.effectPotionTicks > 0) this.entity.effectPotionTicks--;
			if (this.entity.emergencyPotionTicks <= 0 || this.entity.effectPotionTicks <= 0){
				this.effectPotionLevel = this.entity.abilities.getValue(CompanionBatAbility.EFFECT_POTION);
				this.emergencyPotionLevel = this.entity.abilities.getValue(CompanionBatAbility.EMERGENCY_POTION);
				if (this.effectPotionLevel == null || this.effectPotionLevel < 1) this.entity.effectPotionTicks = this.effectPotionCooldown;
				if (this.emergencyPotionLevel == null || this.emergencyPotionLevel < 1) this.entity.emergencyPotionTicks = this.emergencyPotionCooldown;
				if (this.entity.emergencyPotionTicks <= 0 || this.entity.effectPotionTicks <= 0){
					this.owner = livingEntity;
					return true;
				}
			}
			return false;
        }
    }

    public boolean canContinueToUse() {
		return !this.navigation.isDone() && this.owner != null;
    }

    public void start() {
    }

    public void stop() {
        this.owner = null;
    }

    public void tick() {
        if (--this.updateCountdownTicks <= 0) {
			this.updateCountdownTicks = 10;
            if (!this.entity.isLeashed() && !this.entity.isPassenger()) {
				double distance = this.entity.distanceToSqr(this.owner);
				if (distance < this.maxDistanceSquared){
					Potion potion = null;
					if (this.entity.emergencyPotionTicks <= 0){
						if (this.emergencyPotionLevel >= 2 && (this.owner.isOnFire() || this.owner.getLastDamageSource() != null && this.owner.getLastDamageSource().isFire()) && !this.owner.hasEffect(Effects.FIRE_RESISTANCE)) {
							potion = Potions.FIRE_RESISTANCE;
						} else if (this.owner.getHealth() < (this.owner.getMaxHealth() * 40 / 100)) {
							potion = Potions.STRONG_HEALING;
						}
						if (potion != null) this.entity.emergencyPotionTicks = this.emergencyPotionCooldown;
					}

					if (potion == null && this.entity.effectPotionTicks <= 0){
						if (this.effectPotionLevel >= 4 && !this.owner.hasEffect(Effects.REGENERATION) && this.owner.getHealth() < (this.owner.getMaxHealth() * 50 / 100)){
							potion = Potions.REGENERATION;
						} else if (this.effectPotionLevel >= 2 && (!this.owner.hasEffect(Effects.NIGHT_VISION) || this.owner.getEffect(Effects.NIGHT_VISION).getDuration() <= 400) && this.entity.level.getMaxLocalRawBrightness(this.owner.blockPosition()) <= 7){
							potion = Potions.NIGHT_VISION;
						} else if (!this.owner.hasEffect(Effects.MOVEMENT_SPEED) || this.owner.getEffect(Effects.MOVEMENT_SPEED).getDuration() <= 400) {
							potion = Potions.SWIFTNESS;
						}
						if (potion != null) this.entity.effectPotionTicks = this.effectPotionCooldown;
					}

					// TODO: Attack potion

					if (potion != null) {
						Vector3d Vector3d = this.owner.getDeltaMovement();
						double d = this.owner.getX() + Vector3d.x - this.entity.getX();
						double e = this.owner.getEyeY() - 1.100000023841858D - this.entity.getY();
						double f = this.owner.getZ() + Vector3d.z - this.entity.getZ();
						float g = MathHelper.sqrt(d * d + f * f);

						PotionEntity potionEntity = new PotionEntity(this.entity.level, this.entity);
						potionEntity.setItem(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
						potionEntity.xRot += 20.0F;
						potionEntity.shoot(d, e + (double)(g * 0.2F), f, 0.75F, 8.0F);
						if (!this.entity.isSilent()) {
							this.entity.level.playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundEvents.WITCH_THROW, SoundCategory.PLAYERS, 1.0F, 0.8F + this.entity.level.random.nextFloat() * 0.4F);
						}

						this.entity.level.addFreshEntity(potionEntity);
						this.owner = null;
					}
				}
            }
        }
	}
}
