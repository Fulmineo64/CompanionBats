package dev.fulmineo.companion_bats.screen;

import dev.fulmineo.companion_bats.item.CompanionBatItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.Hand;
import net.minecraft.util.text.Component;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class CompanionBatContainerProvider implements INamedContainerProvider {
    private final CompanionBatItem batItem;
    private final Inventory inventory;
    private final Hand hand;

    public CompanionBatContainerProvider(CompanionBatItem batItem, Inventory inventory, Hand hand){
        this.batItem = batItem;
        this.inventory = inventory;
        this.hand = hand;
    }

    public Component getDisplayName() {
        return new TranslationTextComponent(this.batItem.getDescriptionId());
    }

    public @Nullable Container createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new CompanionBatScreenHandler(syncId, inv, this.inventory, this.hand);
    }
}
