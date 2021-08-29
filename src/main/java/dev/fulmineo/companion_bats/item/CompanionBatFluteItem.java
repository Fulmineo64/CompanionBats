package dev.fulmineo.companion_bats.item;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
            CompoundTag tag = fluteItemStack.getTag();
            if (tag != null) {
                CompanionBatEntity entity = (CompanionBatEntity) ((ServerWorld) world).getEntity(tag.getUUID("EntityUUID"));
                if (entity != null) {
                    entity.returnToPlayerInventory();
                    return ActionResult.success(fluteItemStack);
                } else {
					IFormattableTextComponent name = tag.contains("EntityName") ? new StringTextComponent(tag.getString("EntityName")) : new TranslationTextComponent("entity.companion_bats.bat.your_bat");
					user.displayClientMessage(name.append(new TranslationTextComponent("item.companion_bats.bat_flute.fail")), true);
                    return ActionResult.fail(fluteItemStack);
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