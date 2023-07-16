package dev.fulmineo.companion_bats.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.data.CompanionBatClassLevel;
import dev.fulmineo.companion_bats.data.CompanionBatCombatLevel;
import dev.fulmineo.companion_bats.data.EntityData;
import dev.fulmineo.companion_bats.data.ServerDataManager;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class CompanionBatCommandInit {
	public static void init(){
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register(
				CommandManager.literal("batexp")
				.requires(source -> source.hasPermissionLevel(2))
				.then(CommandManager.argument("exp", IntegerArgumentType.integer(1))
					.executes(context -> {
						ServerCommandSource source = context.getSource();
						if (source != null) {
							ServerPlayerEntity player = source.getPlayerOrThrow();
							ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
							if (player != null && (stack.isOf(CompanionBats.BAT_ITEM) || stack.isOf(CompanionBats.NETHERITE_BAT_ITEM))) {
								EntityData entityData = new EntityData(stack);
								int exp = IntegerArgumentType.getInteger(context, "exp");
								CompanionBatCombatLevel maxLevel = ServerDataManager.combatLevels[ServerDataManager.combatLevels.length-1];
								if (exp > maxLevel.totalExp) {
									exp = maxLevel.totalExp;
								}
								entityData.putExp(exp);
							} else {
								context.getSource().sendFeedback(Text.literal("Item in main hand isn't a companion bat in item form"), false);
							}
						}
						return 1;
					})
				)
			);

			dispatcher.register(
				CommandManager.literal("batclassexp")
				.requires(source -> source.hasPermissionLevel(2))
				.then(CommandManager.argument("exp", IntegerArgumentType.integer(1))
					.executes(context -> {
						ServerCommandSource source = context.getSource();
						if (source != null) {
							ServerPlayerEntity player = source.getPlayerOrThrow();
							ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
							if (player != null && (stack.isOf(CompanionBats.BAT_ITEM) || stack.isOf(CompanionBats.NETHERITE_BAT_ITEM))) {
								EntityData entityData = new EntityData(stack);
								NbtElement armorItem = entityData.getArmor();
								if (armorItem != null){
									ItemStack armorStack = ItemStack.fromNbt((NbtCompound)armorItem);
									if (armorStack.getItem() instanceof CompanionBatArmorItem){
										String className = ((CompanionBatArmorItem)armorStack.getItem()).getClassName();
										int exp = IntegerArgumentType.getInteger(context, "exp");
										CompanionBatClassLevel[] classLevels = ServerDataManager.classes.get(className).levels;
										CompanionBatClassLevel maxLevel = classLevels[classLevels.length-1];
										if (exp > maxLevel.totalExp) {
											exp = maxLevel.totalExp;
										}
										entityData.putClassExp(className, exp);
									} else {
										context.getSource().sendFeedback(Text.literal("No class found to set exp"), false);
									}
								}
							} else {
								context.getSource().sendFeedback(Text.literal("Item in main hand isn't a companion bat in item form"), false);
							}
						}
						return 1;
					})
				)
			);
		});
	}
}
