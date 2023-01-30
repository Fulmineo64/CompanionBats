package dev.fulmineo.companion_bats.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;

@Mixin(BundleItem.class)
public interface BundleItemInvoker {
	@Invoker("getBundleOccupancy")
	public static int invokeGetBundleOccupancy(ItemStack stack) {
		throw new AssertionError();
	}

	@Invoker("addToBundle")
	public static int invokeAddToBundle(ItemStack bundle, ItemStack stack) {
		throw new AssertionError();
	}

	@Invoker("getItemOccupancy")
	public static int invokeGetItemOccupancy(ItemStack stack) {
		throw new AssertionError();
	}

	@Invoker("removeFirstStack")
	public static Optional<ItemStack> invokeRemoveFirstStack(ItemStack stack) {
		throw new AssertionError();
	}
}

