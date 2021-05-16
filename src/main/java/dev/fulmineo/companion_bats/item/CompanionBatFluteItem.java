package dev.fulmineo.companion_bats.item;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CompanionBatFluteItem extends Item {

    public CompanionBatFluteItem(Properties settings) {
        super(settings);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack fluteItemStack = user.getItemInHand(hand);
        if (world instanceof ServerWorld) {
            CompoundNBT tag = fluteItemStack.getTag();
            if (tag != null) {
                CompanionBatEntity entity = (CompanionBatEntity) ((ServerWorld) world).getEntity(tag.getUUID("EntityUUID"));
                if (entity != null) {
                    entity.returnToPlayerInventory();
                    return ActionResult.success(fluteItemStack);
                } else {
                    return ActionResult.fail(new ItemStack(Items.AIR));
                }
            }
            return ActionResult.fail(new ItemStack(Items.AIR));
        } else {
            return ActionResult.success(fluteItemStack);
        }
    }

    public boolean isUsedOnRelease(ItemStack stack) {
        return true;
    }
}