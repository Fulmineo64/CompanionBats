package dev.fulmineo.companion_bats.entity;

import dev.fulmineo.companion_bats.CompanionBats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class DynamiteEntity extends ProjectileItemEntity {
	private float power = 1.0F;

	public DynamiteEntity(EntityType<? extends DynamiteEntity> entityType, World world) {
		super(entityType, world);
	}

	public DynamiteEntity(World world, LivingEntity owner) {
		super(CompanionBats.DYNAMITE.get(), owner, world);
	}

	public DynamiteEntity(World world, double x, double y, double z) {
		super(CompanionBats.DYNAMITE.get(), x, y, z, world);
	}

	protected Item getDefaultItem() {
		return Items.TNT;
	}

	protected float getGravity() {
		return 0.05F;
	}

	protected void onHit(RayTraceResult blockHitResult) {
		super.onHit(blockHitResult);
		if (!this.level.isClientSide) {
			this.explode();
		}
	}

	protected void onHitEntity(EntityRayTraceResult hitResult) {
		super.onHitEntity(hitResult);
		if (!this.level.isClientSide) {
			this.explode();
			this.remove();
		}
	}

	public void setPower(float power){
		this.power = power;
	}

	private void explode(){
		this.level.explode(this, this.getX(), this.getY(0.0625D), this.getZ(), this.power, Explosion.Mode.NONE);
	}
}
