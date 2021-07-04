package dev.fulmineo.companion_bats.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.fulmineo.companion_bats.CompanionBats;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;

@Mixin(WanderingTraderEntity.class)
public abstract class WanderingTraderEntityMixin extends MerchantEntity {
    public WanderingTraderEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }


   @Inject(at = @At("RETURN"), method = "fillRecipes")
    public void fillRecipesMixin(CallbackInfo info) {
		TradeOfferList tradeOfferList = this.getOffers();
		if (random.nextDouble() < 0.5D) {
			tradeOfferList.add(new TradeOffer(new ItemStack(Items.EMERALD, 10), new ItemStack(Items.DIAMOND, 5), new ItemStack(CompanionBats.SPIRIT_SHARD, 1), 5, 1, 0.05F));
		}
    }
}
