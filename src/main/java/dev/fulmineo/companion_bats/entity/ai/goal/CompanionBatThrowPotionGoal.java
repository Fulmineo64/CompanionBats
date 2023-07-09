package dev.fulmineo.companion_bats.entity.ai.goal;

import dev.fulmineo.companion_bats.data.CompanionBatAbilityType;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class CompanionBatThrowPotionGoal extends Goal {
    private final CompanionBatEntity entity;
    private LivingEntity owner;
	private final double maxDistanceSquared;
    private final EntityNavigation navigation;
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

    public boolean canStart() {
        LivingEntity livingEntity = this.entity.getOwner();
		if (livingEntity == null || livingEntity.isSpectator() || this.entity.isRoosting()) {
            return false;
        } else {
			if (this.entity.emergencyPotionTicks > 0) this.entity.emergencyPotionTicks--;
			if (this.entity.effectPotionTicks > 0) this.entity.effectPotionTicks--;
			if (this.entity.emergencyPotionTicks <= 0 || this.entity.effectPotionTicks <= 0){
				this.effectPotionLevel = this.entity.abilities.getValue(CompanionBatAbilityType.EFFECT_POTION);
				this.emergencyPotionLevel = this.entity.abilities.getValue(CompanionBatAbilityType.EMERGENCY_POTION);
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

    public boolean shouldContinue() {
		return !this.navigation.isIdle() && this.owner != null;
    }

    public void start() {
    }

    public void stop() {
        this.owner = null;
    }

    public void tick() {
        if (--this.updateCountdownTicks <= 0) {
			this.updateCountdownTicks = 10;
            if (!this.entity.isLeashed() && !this.entity.hasVehicle()) {
				double distance = this.entity.squaredDistanceTo(this.owner);
				if (distance < this.maxDistanceSquared){
					Potion potion = null;
					if (this.entity.emergencyPotionTicks <= 0){
						if (this.emergencyPotionLevel >= 2 && (this.owner.isOnFire() || this.owner.getRecentDamageSource() == this.owner.getDamageSources().inFire()) && !this.owner.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
							potion = Potions.FIRE_RESISTANCE;
						} else if (this.owner.getHealth() < (this.owner.getMaxHealth() * 40 / 100)) {
							potion = Potions.STRONG_HEALING;
						}
						if (potion != null) this.entity.emergencyPotionTicks = this.emergencyPotionCooldown;
					}

					if (potion == null && this.entity.effectPotionTicks <= 0){
						if (this.effectPotionLevel >= 4 && !this.owner.hasStatusEffect(StatusEffects.REGENERATION) && this.owner.getHealth() < (this.owner.getMaxHealth() * 50 / 100)){
							potion = Potions.REGENERATION;
						} else if (this.effectPotionLevel >= 2 && (!this.owner.hasStatusEffect(StatusEffects.NIGHT_VISION) || this.owner.getStatusEffect(StatusEffects.NIGHT_VISION).getDuration() <= 400) && this.entity.world.getLightLevel(this.owner.getBlockPos()) <= 7){
							potion = Potions.NIGHT_VISION;
						} else if (!this.owner.hasStatusEffect(StatusEffects.SPEED) || this.owner.getStatusEffect(StatusEffects.SPEED).getDuration() <= 400) {
							potion = Potions.SWIFTNESS;
						} else if (this.effectPotionLevel >= 3 && !this.owner.hasStatusEffect(StatusEffects.STRENGTH)) {
							potion = Potions.STRENGTH;
						}
						if (potion != null) this.entity.effectPotionTicks = this.effectPotionCooldown;
					}

					// TODO: Attack potion

					if (potion != null) {
						Vec3d vec3d = this.owner.getVelocity();
						double d = this.owner.getX() + vec3d.x - this.entity.getX();
						double e = this.owner.getEyeY() - 1.100000023841858D - this.entity.getY();
						double f = this.owner.getZ() + vec3d.z - this.entity.getZ();
						double g = Math.sqrt(d * d + f * f);

						PotionEntity potionEntity = new PotionEntity(this.entity.world, this.entity);
						potionEntity.setItem(PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
						potionEntity.setPitch(potionEntity.getPitch() + 20.0F);
						potionEntity.setVelocity(d, e + g * 0.2D, f, 0.75F, 8.0F);
						if (!this.entity.isSilent()) {
							this.entity.world.playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundEvents.ENTITY_WITCH_THROW, SoundCategory.PLAYERS, 1.0F, 0.8F + this.entity.world.random.nextFloat() * 0.4F);
						}

						this.entity.world.spawnEntity(potionEntity);
						this.owner = null;
					}
				}
            }
        }
	}
}
