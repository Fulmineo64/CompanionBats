package dev.fulmineo.companion_bats.item;

import java.util.Iterator;
import java.util.List;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CompanionBatCommandFluteRestItem extends Item {

	public CompanionBatCommandFluteRestItem(Settings settings) {
        super(settings);
    }

	@Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

		if (user.isSneaking()){
			itemStack = new ItemStack(CompanionBats.COMMAND_FLUTE_GUARD);
		} else {
			if (world instanceof ServerWorld) {
				List<CompanionBatEntity> list = CompanionBatEntity.getPlayerBats((ServerPlayerEntity)user);
				Iterator<CompanionBatEntity> iterator = list.iterator();
				while(iterator.hasNext()) {
					CompanionBatEntity entity = iterator.next();
					entity.setTarget(null);
					entity.startRoosting();
				}
			}

			user.getItemCooldownManager().set(CompanionBats.COMMAND_FLUTE_REST, 10);
		}

		return TypedActionResult.success(itemStack);
    }

}
