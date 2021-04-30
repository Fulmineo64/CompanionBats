package dev.fulmineo.companion_bats.item;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CompanionBatStaffItem extends Item {

	public CompanionBatStaffItem(Settings settings) {
        super(settings);
    }

	@Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
		NbtCompound tag = itemStack.getOrCreateTag();
		Byte mode = tag.getByte("mode");

		if (user.isSneaking()){
			if (mode == (byte)1) {
				tag.remove("mode");
			} else {
				tag.putByte("mode", (byte)1);
			}
		} else {
			double distance = 20.0D;
			Vec3d vec3d2 = user.getRotationVec(1.0F);
			Box box = user.getBoundingBox().stretch(vec3d2.multiply(distance)).expand(1.0D, 1.0D, 1.0D);

			switch (mode) {
				case 1: {
					if (world instanceof ServerWorld) {
						TargetPredicate predicate = new TargetPredicate().setPredicate((livingEntity) -> !livingEntity.isSpectator() && livingEntity != user && CompanionBatEntity.canAttackWithOwnerStatic(livingEntity, user));
						LivingEntity entity = world.getClosestEntity(LivingEntity.class, predicate, user, user.getX(), user.getY(), user.getZ(), box);
						if (entity != null) {
							entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10, 0, false, false));
							user.setAttacker(entity);
						}
					}
					break;
				}
				default: {
					if (world instanceof ServerWorld) {
						Vec3d vec3d = user.getCameraPosVec(1.0F);
						Vec3d vec3d3 = vec3d.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
						EntityHitResult entityHitResult = ProjectileUtil.raycast(user, vec3d, vec3d3, box, (entityx) -> !entityx.isSpectator() && entityx.collides() && entityx instanceof LivingEntity && CompanionBatEntity.canAttackWithOwnerStatic((LivingEntity)entityx, user), distance * distance);
						if (entityHitResult != null) {
							LivingEntity entity = (LivingEntity)entityHitResult.getEntity();
							entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10, 0, false, false));
							user.setAttacker(entity);
						}
					}
				}
			}

			user.getItemCooldownManager().set(CompanionBats.BAT_STAFF, 10);
		}

		return TypedActionResult.success(itemStack);
    }

}
