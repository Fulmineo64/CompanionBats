package dev.fulmineo.companion_bats.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CompanionBatPouchItem extends Item {

    public CompanionBatPouchItem(Settings settings) {
        super(settings);
    }

	@Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
		CompoundTag tag = itemStack.getOrCreateTag();
		ItemStack containedItemStack = ItemStack.fromTag(tag.getCompound("item"));
		if (!containedItemStack.isEmpty()){
			user.inventory.insertStack(containedItemStack);
			tag.remove("item");
			world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.AMBIENT, 0.3F, 1F);
		}
        return TypedActionResult.success(itemStack);
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
			tag.put("item", stackToAdd.toTag(new CompoundTag()));
			return true;
		} else {
			ItemStack currentItem = ItemStack.fromTag(tag.getCompound("item"));
			if (currentItem.getItem() == stackToAdd.getItem()){
				int count = currentItem.getCount() + stackToAdd.getCount();
				int remainder = count > 64 ? 64 - count : 0;
				stackToAdd.setCount(remainder);
				currentItem.setCount(count);
			}
		}
		return false;
	}

	public static ItemStack getItem(ItemStack pouchItemStack){
		CompoundTag tag = pouchItemStack.getTag();
		if (tag.contains("item")){
			ItemStack result = ItemStack.fromTag(tag.getCompound("item"));
			result.setTag(null);
			tag.remove("item");
			return result;
		}
		return null;
	}

}
