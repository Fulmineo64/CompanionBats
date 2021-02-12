package dev.fulmineo.companion_bats.init;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels.CompanionBatClassLevel;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels.CompanionBatLevel;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import dev.fulmineo.companion_bats.item.CompanionBatClass;
import dev.fulmineo.companion_bats.item.CompanionBatItem;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Hand;

public class CompanionBatCommandInit {
	public static void init(){
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
				CommandManager.literal("batexp")
				.requires(source -> source.hasPermissionLevel(2))
				.then(CommandManager.argument("exp", IntegerArgumentType.integer(1))
					.executes(context -> {
						ServerCommandSource source = context.getSource();
						if (source != null) {
							ServerPlayerEntity player = source.getPlayer();
							if (player != null && player.getStackInHand(Hand.MAIN_HAND).getItem() == CompanionBats.BAT_ITEM) {
								ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
								CompoundTag entityData = CompanionBatItem.getOrCreateEntityData(stack);
								int exp = IntegerArgumentType.getInteger(context, "exp");
								CompanionBatLevel maxLevel = CompanionBatLevels.LEVELS[CompanionBatLevels.LEVELS.length-1];
								if (exp > maxLevel.totalExpNeeded) {
									exp = maxLevel.totalExpNeeded;
								}
								entityData.putInt("exp", exp);
							} else {
								context.getSource().sendFeedback(new LiteralText("Item in main hand isn't a companion bat in item form"), false);
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
							ServerPlayerEntity player = source.getPlayer();
							if (player != null && player.getStackInHand(Hand.MAIN_HAND).getItem() == CompanionBats.BAT_ITEM) {
								ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
								CompoundTag entityData = CompanionBatItem.getOrCreateEntityData(stack);
								ItemStack armorStack = ItemStack.fromTag(entityData.getCompound("armor"));
								if (armorStack.getItem() instanceof CompanionBatArmorItem){
									CompanionBatClass cls = ((CompanionBatArmorItem)armorStack.getItem()).getBatClass();
									int exp = IntegerArgumentType.getInteger(context, "exp");
									CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(cls);
									CompanionBatClassLevel maxLevel = classLevels[classLevels.length-1];
									if (exp > maxLevel.totalExpNeeded) {
										exp = maxLevel.totalExpNeeded;
									}
									entityData.putInt(cls.toString().toLowerCase()+"_exp", exp);
								} else {
									context.getSource().sendFeedback(new LiteralText("No class found to set exp"), false);
								}
							} else {
								context.getSource().sendFeedback(new LiteralText("Item in main hand isn't a companion bat in item form"), false);
							}
						}
						return 1;
					})
				)
			);
		});
	}
}
