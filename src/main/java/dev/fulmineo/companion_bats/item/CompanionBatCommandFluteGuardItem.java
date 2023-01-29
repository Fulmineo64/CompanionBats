package dev.fulmineo.companion_bats.item;

import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CompanionBatCommandFluteGuardItem extends Item {
	private byte currentMode = 0;

	public CompanionBatCommandFluteGuardItem(Settings settings) {
        super(settings);
    }

	@Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

		if (user.isSneaking()){
			itemStack = new ItemStack(CompanionBats.COMMAND_FLUTE_ATTACK);
		} else {
			if (world instanceof ServerWorld) {
				this.currentMode++;
				if (this.currentMode > 2) this.currentMode = 0;
				List<CompanionBatEntity> list = CompanionBatEntity.getPlayerBats((ServerPlayerEntity)user);
				Iterator<CompanionBatEntity> iterator = list.iterator();
				while(iterator.hasNext()) {
					CompanionBatEntity entity = iterator.next();
					entity.setGuardMode((byte)(this.currentMode));
				}
				user.sendMessage(Text.translatable("item.companion_bats.command_flute_guard.mode."+currentMode), true);
			}

			user.getItemCooldownManager().set(CompanionBats.COMMAND_FLUTE_REST, 10);
		}

		return TypedActionResult.success(itemStack);
    }

	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("item.companion_bats.command_flute_guard.description.0").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("item.companion_bats.command_flute.description_end").formatted(Formatting.DARK_GRAY));
	}
}
