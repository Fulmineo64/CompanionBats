package dev.fulmineo.companion_bats.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CompanionBatPouchItem extends Item {

    public CompanionBatPouchItem(Properties settings) {
        super(settings);
    }

	@Override
    public ActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
		CompoundTag tag = itemStack.getOrCreateTag();
		ItemStack containedItemStack = ItemStack.of(tag.getCompound("item"));
		if (!containedItemStack.isEmpty()){
			user.inventory.add(containedItemStack);
			tag.remove("item");
			world.playSound(null, new BlockPos(user.getX(), user.getY(), user.getZ()), SoundEvents.ITEM_PICKUP, SoundCategory.AMBIENT, 0.3F, 1F);
		}
        return ActionResult.success(itemStack);
    }

	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<Component> tooltip, ITooltipFlag flag) {
		CompoundTag tag = stack.getOrCreateTag();
		ItemStack containedItemStack = ItemStack.of(tag.getCompound("item"));
		if (!containedItemStack.isEmpty()){
			IFormattableTextComponent itemText = containedItemStack.getDisplayName().copy();
			itemText.append(" x").append(String.valueOf(containedItemStack.getCount()));
			tooltip.add(itemText.withStyle(ChatFormatting.GRAY));
		}
	}

	public static boolean isEmpty(ItemStack pouchItemStack){
		CompoundTag tag = pouchItemStack.getOrCreateTag();
		if (tag.contains("item")){
			return false;
		}
		return true;
	}

	public static boolean addItem(ItemStack pouchItemStack, ItemStack stackToAdd){
		CompoundTag tag = pouchItemStack.getOrCreateTag();
		if (!tag.contains("item")){
			tag.put("item", stackToAdd.save(new CompoundTag()));
			return true;
		}
		return false;
	}

	public static ItemStack getItem(ItemStack pouchItemStack){
		CompoundTag tag = pouchItemStack.getTag();
		if (tag.contains("item")){
			ItemStack result = ItemStack.of(tag.getCompound("item"));
			tag.remove("item");
			return result;
		}
		return null;
	}

}
