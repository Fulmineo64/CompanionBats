package dev.fulmineo.companion_bats.item;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CompanionBatPouchItem extends Item {

    public CompanionBatPouchItem(Properties settings) {
        super(settings);
    }

	@Override
    public ActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
		CompoundNBT tag = itemStack.getOrCreateTag();
		ItemStack containedItemStack = ItemStack.of(tag.getCompound("item"));
		if (!containedItemStack.isEmpty()){
			user.inventory.add(containedItemStack);
			tag.remove("item");
			world.playSound(null, new BlockPos(user.getX(), user.getY(), user.getZ()), SoundEvents.ITEM_PICKUP, SoundCategory.AMBIENT, 0.3F, 1F);
		}
        return ActionResult.success(itemStack);
    }

	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		CompoundNBT tag = stack.getOrCreateTag();
		ItemStack containedItemStack = ItemStack.of(tag.getCompound("item"));
		if (!containedItemStack.isEmpty()){
			IFormattableTextComponent itemText = containedItemStack.getDisplayName().copy();
			itemText.append(" x").append(String.valueOf(containedItemStack.getCount()));
			tooltip.add(itemText.withStyle(TextFormatting.GRAY));
		}
	}

	public static boolean isEmpty(ItemStack pouchItemStack){
		CompoundNBT tag = pouchItemStack.getOrCreateTag();
		if (tag.contains("item")){
			return false;
		}
		return true;
	}

	public static boolean addItem(ItemStack pouchItemStack, ItemStack stackToAdd){
		CompoundNBT tag = pouchItemStack.getOrCreateTag();
		if (!tag.contains("item")){
			tag.put("item", stackToAdd.save(new CompoundNBT()));
			return true;
		}
		return false;
	}

	public static ItemStack getItem(ItemStack pouchItemStack){
		CompoundNBT tag = pouchItemStack.getTag();
		if (tag.contains("item")){
			ItemStack result = ItemStack.of(tag.getCompound("item"));
			tag.remove("item");
			return result;
		}
		return null;
	}

}
