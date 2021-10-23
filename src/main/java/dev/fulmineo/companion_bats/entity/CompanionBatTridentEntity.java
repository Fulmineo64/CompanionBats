package dev.fulmineo.companion_bats.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
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
			super.onEntityHit(entityHitResult);
			CompanionBatEntity owner = (CompanionBatEntity)this.getOwner();
			if (owner != null) {
				owner.applyOnHitEffects(livingEntity, targetHealth, livingEntity.getHealth());
			}
		} else {
			super.onEntityHit(entityHitResult);
		}
	}
}
