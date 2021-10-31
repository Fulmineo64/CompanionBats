package dev.fulmineo.companion_bats.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class CompanionBatTridentEntity extends TridentEntity {
	public CompanionBatTridentEntity(EntityType<? extends TridentEntity> entityType, World world) {
		super(entityType, world);
	}

	public CompanionBatTridentEntity(World world, LivingEntity owner, ItemStack stack) {
		super(world, owner, stack);
	}

	protected void onEntityHit(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)entity;
			float targetHealth = livingEntity.getHealth();
			this.tridentOnEntityHit(entityHitResult);
			Entity owner = this.getOwner();
			if (owner != null) {
				((CompanionBatEntity)owner).applyOnHitEffects(livingEntity, targetHealth, livingEntity.getHealth());
			}
		} else {
			this.tridentOnEntityHit(entityHitResult);
		}
	}

	protected void tridentOnEntityHit(EntityHitResult entityHitResult) {
		Entity entity = entityHitResult.getEntity();
		float f = 0F;
		Entity owner = this.getOwner();
		if (owner != null) {
			f = ((CompanionBatEntity)owner).getAttackDamage(entity);
		}
		Entity entity2 = this.getOwner();
		DamageSource damageSource = DamageSource.trident(this, (Entity)(entity2 == null ? this : entity2));
		if (entity.damage(damageSource, f)) {
			if (entity.getType() == EntityType.ENDERMAN) {
				return;
			}
			if (entity instanceof LivingEntity) {
				LivingEntity livingEntity2 = (LivingEntity)entity;
				if (entity2 instanceof LivingEntity) {
				   EnchantmentHelper.onUserDamaged(livingEntity2, entity2);
				   EnchantmentHelper.onTargetDamaged((LivingEntity)entity2, livingEntity2);
				}
				this.onHit(livingEntity2);
			}
		}

		this.setVelocity(this.getVelocity().multiply(-0.01D, -0.1D, -0.01D));
		this.playSound(SoundEvents.ITEM_TRIDENT_HIT, 1.0F, 1.0F);
	}
}
