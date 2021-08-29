package dev.fulmineo.companion_bats.screen;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import dev.fulmineo.companion_bats.item.CompanionBatAccessoryItem;
import dev.fulmineo.companion_bats.nbt.EntityData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CompanionBatScreenHandler extends Container {
    private final Inventory inventory;
    public Hand hand;

    public CompanionBatScreenHandler(int syncId, PlayerInventory playerInventory/*, PacketBuffer buf*/){
    	// TODO: prendere hand da PacketBuffer
        this(syncId, playerInventory, new Inventory(3), Hand.MAIN_HAND/*, buf.readEnum(Hand.class)*/);
    }

    public CompanionBatScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, Hand hand) {
        super(CompanionBats.BAT_SCREEN_HANDLER.get(), syncId);
        this.inventory = inventory;
        this.hand = hand;
		inventory.startOpen(playerInventory.player);

		this.addSlot(new Slot(inventory, 0, 8, 18) {
            public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof CompanionBatAccessoryItem;
            }

			@Override
            public void set(ItemStack stack) {
                super.set(stack);
                PlayerEntity player = playerInventory.player;
				ItemStack batItemStack = player.getItemInHand(hand);
				if (batItemStack.getItem() == CompanionBats.BAT_ITEM.get()){
					EntityData entityData = new EntityData(batItemStack);
					entityData.putAccessory(stack.save(new CompoundTag()));
					entityData.toStack(batItemStack);
				}
            }

            @OnlyIn(Dist.CLIENT)
            public boolean isActive() {
                return true;
            }
		});

		this.addSlot(new Slot(inventory, 1, 8, 36) {
            public boolean mayPlace(ItemStack stack) {
				return stack.getItem() instanceof CompanionBatArmorItem;
            }

			@Override
            public void set(ItemStack stack) {
                super.set(stack);
                PlayerEntity player = playerInventory.player;
				ItemStack batItemStack = player.getItemInHand(hand);
				if (batItemStack.getItem() == CompanionBats.BAT_ITEM.get()){
					EntityData entityData = new EntityData(batItemStack);
					entityData.putArmor(stack.save(new CompoundTag()));
					entityData.toStack(batItemStack);
				}
            }

            @OnlyIn(Dist.CLIENT)
            public boolean isActive() {
                return true;
            }
		});

        this.addSlot(new Slot(inventory, 2, 8, 54) {
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == CompanionBats.BAT_POUCH_ITEM.get();
            }

            @Override
            public void set(ItemStack stack) {
                super.set(stack);
                PlayerEntity player = playerInventory.player;
                if (player.level instanceof ServerWorld){
                    ItemStack batItemStack = player.getItemInHand(hand);
                    if (batItemStack.getItem() == CompanionBats.BAT_ITEM.get()){
						EntityData entityData = new EntityData(batItemStack);
						entityData.putBundle(stack.save(new CompoundTag()));
						entityData.toStack(batItemStack);
                    }
                }
			}

            @OnlyIn(Dist.CLIENT)
            public boolean isActive() {
                return true;
            }
		});

        int o;
        int n;

        for(o = 0; o < 3; ++o) {
            for(n = 0; n < 9; ++n) {
            	this.addSlot(new Slot(playerInventory, n + o * 9 + 9, 8 + n * 18, 102 + o * 18 + -18));
            }
        }

        for(o = 0; o < 9; ++o) {
			if (playerInventory.getItem(o).getItem() == CompanionBats.BAT_ITEM.get()){
				this.addSlot(new DisabledSlot(playerInventory, o, 8 + o * 18, 142));
			} else {
				this.addSlot(new Slot(playerInventory, o, 8 + o * 18, 142));
			}
        }
    }

    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    public ItemStack quickMoveStack(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
		   	ItemStack itemStack2 = slot.getItem();
		   	itemStack = itemStack2.copy();
		   	int invSize = this.inventory.getContainerSize();
			if (index < invSize) {
				if (!this.moveItemStackTo(itemStack2, invSize, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(0).mayPlace(itemStack2) && !this.getSlot(0).hasItem()) {
				if (!this.moveItemStackTo(itemStack2, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(1).mayPlace(itemStack2) && !this.getSlot(1).hasItem()) {
				if (!this.moveItemStackTo(itemStack2, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(2).mayPlace(itemStack2) && !this.getSlot(2).hasItem()) {
				if (!this.moveItemStackTo(itemStack2, 2, 3, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= invSize && index < 27 + invSize) {
				if (!this.moveItemStackTo(itemStack2, 27 + invSize, 36 + invSize, false)) {
				   return ItemStack.EMPTY;
				}
			} else if (index >= (27 + invSize) && index < (36 + invSize) && !this.moveItemStackTo(itemStack2, invSize, 27 + invSize, false)) {
				return ItemStack.EMPTY;
			}
			if (itemStack2.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}
		return itemStack;
	}

    public void removed(PlayerEntity player) {
        super.removed(player);
        this.inventory.stopOpen(player);
    }

	public class DisabledSlot extends Slot {
		public DisabledSlot(PlayerInventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		public boolean mayPickup(PlayerEntity playerEntity) {
			return false;
		}

		public boolean mayPlace(ItemStack stack) {
			return false;
		}
	}
}
