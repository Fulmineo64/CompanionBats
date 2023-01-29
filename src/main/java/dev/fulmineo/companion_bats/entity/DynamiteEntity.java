package dev.fulmineo.companion_bats.entity;

import dev.fulmineo.companion_bats.CompanionBats;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class DynamiteEntity extends ThrownItemEntity {
	private float power = 1.0F;

	public DynamiteEntity(EntityType<? extends DynamiteEntity> entityType, World world) {
		super(entityType, world);
	}

	public DynamiteEntity(World world, LivingEntity owner) {
		super(CompanionBats.DYNAMITE, owner, world);
	}

	public DynamiteEntity(World world, double x, double y, double z) {
		super(CompanionBats.DYNAMITE, x, y, z, world);
	}

	protected Item getDefaultItem() {
		return Items.TNT;
	}

	protected float getGravity() {
		return 0.05F;
	}

	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		if (!this.world.isClient) {
			this.explode();
		}
	}

	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.world.isClient) {
			this.explode();
			this.discard();
		}
	}

	public void setPower(float power){
		this.power = power;
	}

	private void explode(){
		this.world.createExplosion(this, this.getX(), this.getBodyY(0.0625D), this.getZ(), this.power, ExplosionSourceType.NONE);
	}
}
