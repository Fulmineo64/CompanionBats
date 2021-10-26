package dev.fulmineo.companion_bats.screen;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.data.EntityData;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import dev.fulmineo.companion_bats.item.CompanionBatAccessoryItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

public class CompanionBatScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    public Hand hand;

    public CompanionBatScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf){
        this(syncId, playerInventory, new SimpleInventory(3), buf.readEnumConstant(Hand.class));
    }

    public CompanionBatScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, Hand hand) {
        super(CompanionBats.BAT_SCREEN_HANDLER, syncId);
        this.inventory = inventory;
        this.hand = hand;
		inventory.onOpen(playerInventory.player);

		this.addSlot(new Slot(inventory, 0, 8, 18) {
            public boolean canInsert(ItemStack stack) {
				return stack.getItem() instanceof CompanionBatAccessoryItem;
            }

			@Override
            public void setStack(ItemStack stack) {
                super.setStack(stack);
                PlayerEntity player = playerInventory.player;
				ItemStack batItemStack = player.getStackInHand(hand);
				if (batItemStack.isOf(CompanionBats.BAT_ITEM)){
					EntityData entityData = new EntityData(batItemStack);
					entityData.putAccessory(stack.writeNbt(new NbtCompound()));
					entityData.toStack(batItemStack);
				}
            }

            @Environment(EnvType.CLIENT)
            public boolean isEnabled() {
                return true;
            }
		});

		this.addSlot(new Slot(inventory, 1, 8, 36) {
            public boolean canInsert(ItemStack stack) {
				return stack.getItem() instanceof CompanionBatArmorItem;
            }

			@Override
            public void setStack(ItemStack stack) {
                super.setStack(stack);
                PlayerEntity player = playerInventory.player;
				ItemStack batItemStack = player.getStackInHand(hand);
				if (batItemStack.isOf(CompanionBats.BAT_ITEM)){
					EntityData entityData = new EntityData(batItemStack);
					entityData.putArmor(stack.writeNbt(new NbtCompound()));
					entityData.toStack(batItemStack);
				}
            }

            @Environment(EnvType.CLIENT)
            public boolean isEnabled() {
                return true;
            }
		});

        this.addSlot(new Slot(inventory, 2, 8, 54) {
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(Items.BUNDLE);
            }

            @Override
            public void setStack(ItemStack stack) {
                super.setStack(stack);
                PlayerEntity player = playerInventory.player;
                if (player.world instanceof ServerWorld){
                    ItemStack batItemStack = player.getStackInHand(hand);
                    if (batItemStack.isOf(CompanionBats.BAT_ITEM)){
						EntityData entityData = new EntityData(batItemStack);
						entityData.putBundle(stack.writeNbt(new NbtCompound()));
						entityData.toStack(batItemStack);
                    }
                }
			}

            @Environment(EnvType.CLIENT)
            public boolean isEnabled() {
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
			if (playerInventory.getStack(o).isOf(CompanionBats.BAT_ITEM)){
				this.addSlot(new DisabledSlot(playerInventory, o, 8 + o * 18, 142));
			} else {
				this.addSlot(new Slot(playerInventory, o, 8 + o * 18, 142));
			}
        }
    }

    public boolean canUse(PlayerEntity player) {
        return true;
    }

    public ItemStack transferSlot(PlayerEntity player, int index) {
		ItemStack itemStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.slots.get(index);
		if (slot != null && slot.hasStack()) {
		   	ItemStack itemStack2 = slot.getStack();
		   	itemStack = itemStack2.copy();
		   	int invSize = this.inventory.size();
			if (index < invSize) {
				if (!this.insertItem(itemStack2, invSize, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(0).canInsert(itemStack2) && !this.getSlot(0).hasStack()) {
				if (!this.insertItem(itemStack2, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(1).canInsert(itemStack2) && !this.getSlot(1).hasStack()) {
				if (!this.insertItem(itemStack2, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(2).canInsert(itemStack2) && !this.getSlot(2).hasStack()) {
				if (!this.insertItem(itemStack2, 2, 3, false)) {
					return ItemStack.EMPTY;
				}
			} else if (index >= invSize && index < 27 + invSize) {
				if (!this.insertItem(itemStack2, 27 + invSize, 36 + invSize, false)) {
				   return ItemStack.EMPTY;
				}
			} else if (index >= (27 + invSize) && index < (36 + invSize) && !this.insertItem(itemStack2, invSize, 27 + invSize, false)) {
				return ItemStack.EMPTY;
			}
			if (itemStack2.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
		}
		return itemStack;
	}

    public void close(PlayerEntity player) {
        super.close(player);
        this.inventory.onClose(player);
    }

	public class DisabledSlot extends Slot {
		public DisabledSlot(Inventory inventory, int index, int x, int y) {
			super(inventory, index, x, y);
		}

		public boolean canTakeItems(PlayerEntity playerEntity) {
			return false;
		}

		public boolean canInsert(ItemStack stack) {
			return false;
		}
	}
}
