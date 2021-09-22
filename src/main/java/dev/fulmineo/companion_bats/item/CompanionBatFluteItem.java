package dev.fulmineo.companion_bats.item;

import dev.fulmineo.companion_bats.entity.CompanionBatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CompanionBatFluteItem extends Item {

    public CompanionBatFluteItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack fluteItemStack = user.getStackInHand(hand);
        if (world instanceof ServerWorld) {
            NbtCompound tag = fluteItemStack.getNbt();
            if (tag != null) {
                CompanionBatEntity entity = (CompanionBatEntity) ((ServerWorld) world).getEntity(tag.getUuid("EntityUUID"));
                if (entity != null) {
                    entity.returnToPlayerInventory();
                    return TypedActionResult.success(fluteItemStack);
                } else {
					MutableText name = tag.contains("EntityName") ? new LiteralText(tag.getString("EntityName")) : new TranslatableText("entity.companion_bats.bat.your_bat");
					user.sendMessage(name.append(new TranslatableText("item.companion_bats.bat_flute.fail")), true);
                    return TypedActionResult.fail(fluteItemStack);
                }
            }
            return TypedActionResult.fail(new ItemStack(Items.AIR));
        } else {
            return TypedActionResult.success(fluteItemStack);
        }
    }

    public boolean isUsedOnRelease(ItemStack stack) {
        return true;
    }
}