package dev.fulmineo.companion_bats.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import dev.fulmineo.companion_bats.item.CompanionBatBundleItem;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(BundleItem.class)
public abstract class BundleItemMixin extends Item implements CompanionBatBundleItem {
    @Shadow
    private static int addToBundle(ItemStack bundle, ItemStack stack) { return 0; }
    @Shadow
    private static int getItemOccupancy(ItemStack stack) { return 0; }
	@Shadow
    private static int getBundleOccupancy(ItemStack stack) { return 0; }
    @Shadow
    private static Optional<ItemStack> removeFirstStack(ItemStack stack) { return null; }

    public BundleItemMixin(Item.Settings settings) {
        super(settings);
    }

	public float companionBatsGetAmountFilled(ItemStack stack) {
		return (float)getBundleOccupancy(stack) / 64.0F;
	}

    public int companionBatsAddToBundle(ItemStack bundle, ItemStack stack){
        return addToBundle(bundle, stack);
    }

    public int companionBatsGetItemOccupancy(ItemStack stack){
        return getItemOccupancy(stack);
    }

    public Optional<ItemStack> companionBatsRemoveFirstStack(ItemStack stack){
        return removeFirstStack(stack);
    }
}
