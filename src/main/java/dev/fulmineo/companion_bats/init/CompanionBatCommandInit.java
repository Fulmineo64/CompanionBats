package dev.fulmineo.companion_bats.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels.CompanionBatClassLevel;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels.CompanionBatLevel;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import dev.fulmineo.companion_bats.CompanionBatClass;
import dev.fulmineo.companion_bats.nbt.EntityData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;

public class CompanionBatCommandInit {
	public static void init(final CommandDispatcher<CommandSource> dispatcher){
		dispatcher.register(
			Commands.literal("batexp")
			.requires(source -> source.hasPermission(2))
			.then(Commands.argument("exp", IntegerArgumentType.integer(1))
				.executes(context -> {
					CommandSource source = context.getSource();
					if (source != null) {
						ServerPlayerEntity player = source.getPlayerOrException();
						if (player != null && player.getItemInHand(Hand.MAIN_HAND).getItem() == CompanionBats.BAT_ITEM.get()) {
							ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
							EntityData entityData = new EntityData(stack);
							int exp = IntegerArgumentType.getInteger(context, "exp");
							CompanionBatLevel maxLevel = CompanionBatLevels.LEVELS[CompanionBatLevels.LEVELS.length-1];
							if (exp > maxLevel.totalExpNeeded) {
								exp = maxLevel.totalExpNeeded;
							}
							entityData.putExp(exp);
						} else {
							context.getSource().sendSuccess(new StringTextComponent("Item in main hand isn't a companion bat in item form"), false);
						}
					}
					return 1;
				})
			)
		);

		dispatcher.register(
			Commands.literal("batclassexp")
			.requires(source -> source.hasPermission(2))
			.then(Commands.argument("exp", IntegerArgumentType.integer(1))
				.executes(context -> {
					CommandSource source = context.getSource();
					if (source != null) {
						ServerPlayerEntity player = source.getPlayerOrException();
						if (player != null && player.getItemInHand(Hand.MAIN_HAND).getItem() == CompanionBats.BAT_ITEM.get()) {
							ItemStack stack = player.getItemInHand(Hand.MAIN_HAND);
							EntityData entityData = new EntityData(stack);
							CompoundNBT armorItem = entityData.getArmor();
							if (armorItem != null){
								ItemStack armorStack = ItemStack.of(armorItem);
								if (armorStack.getItem() instanceof CompanionBatArmorItem){
									CompanionBatClass cls = ((CompanionBatArmorItem)armorStack.getItem()).getBatClass();
									int exp = IntegerArgumentType.getInteger(context, "exp");
									CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(cls);
									CompanionBatClassLevel maxLevel = classLevels[classLevels.length-1];
									if (exp > maxLevel.totalExpNeeded) {
										exp = maxLevel.totalExpNeeded;
									}
									entityData.putClassExp(cls, exp);
								} else {
									context.getSource().sendSuccess(new StringTextComponent("No class found to set exp"), false);
								}
							}
						} else {
							context.getSource().sendSuccess(new StringTextComponent("Item in main hand isn't a companion bat in item form"), false);
						}
					}
					return 1;
				})
			)
		);
	}
}
