package dev.fulmineo.companion_bats.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CompanionBatCommandFluteAttackItem extends Item {

	public CompanionBatCommandFluteAttackItem(Settings settings) {
        super(settings);
    }

	@Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

		if (user.isSneaking()){
			itemStack = new ItemStack(CompanionBats.COMMAND_FLUTE_REST);
		} else {
			if (world instanceof ServerWorld) {
				double distance = 20.0D;
				Vec3d vec3d = user.getCameraPosVec(1.0F);
				Vec3d vec3d2 = user.getRotationVec(1.0F);
				Vec3d vec3d3 = vec3d.add(vec3d2.x * distance, vec3d2.y * distance, vec3d2.z * distance);
				Box box = user.getBoundingBox().stretch(vec3d2.multiply(distance)).expand(1.0D, 1.0D, 1.0D);
				EntityHitResult entityHitResult = ProjectileUtil.raycast(user, vec3d, vec3d3, box, (entityx) -> !entityx.isSpectator() && entityx.canHit() && entityx instanceof LivingEntity && CompanionBatEntity.canAttackWithOwnerStatic((LivingEntity)entityx, user), distance * distance);
				if (entityHitResult != null) {
					LivingEntity entity = (LivingEntity)entityHitResult.getEntity();
					entity.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10, 0, false, false));
					user.onAttacking(entity);
				}
			}

			user.getItemCooldownManager().set(CompanionBats.COMMAND_FLUTE_ATTACK, 10);
		}

		return TypedActionResult.success(itemStack);
    }

	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("item.companion_bats.command_flute.description.0").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("item.companion_bats.command_flute.description_end").formatted(Formatting.DARK_GRAY));
	}
}
