package dev.fulmineo.companion_bats.item;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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

	@Environment(EnvType.CLIENT)
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		CompoundTag tag = stack.getOrCreateTag();
		ItemStack containedItemStack = ItemStack.fromTag(tag.getCompound("item"));
		if (!containedItemStack.isEmpty()){
			MutableText itemText = containedItemStack.getName().shallowCopy();
			itemText.append(" x").append(String.valueOf(containedItemStack.getCount()));
			tooltip.add(itemText.formatted(Formatting.GRAY));
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
			tag.put("item", stackToAdd.toTag(new CompoundTag()));
			return true;
		}
		return false;
	}

	public static ItemStack getItem(ItemStack pouchItemStack){
		CompoundTag tag = pouchItemStack.getTag();
		if (tag.contains("item")){
			ItemStack result = ItemStack.fromTag(tag.getCompound("item"));
			tag.remove("item");
			return result;
		}
		return null;
	}

}
