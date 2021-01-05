package dev.fulmineo.companion_bats.mixin;

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

    public BundleItemMixin(Item.Settings settings) {
        super(settings);
    }

    public int addToBundlePublic(ItemStack bundle, ItemStack stack){
        return addToBundle(bundle, stack);
    }
}
