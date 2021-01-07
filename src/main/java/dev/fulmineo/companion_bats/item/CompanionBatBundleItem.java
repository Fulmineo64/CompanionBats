package dev.fulmineo.companion_bats.item;

import java.util.Optional;

import net.minecraft.item.ItemStack;

public interface CompanionBatBundleItem {
    public int companionBatsAddToBundle(ItemStack bundle, ItemStack stack);
    public int companionBatsGetItemOccupancy(ItemStack stack);
    public Optional<ItemStack> companionBatsGetFirstStack(ItemStack stack);
}