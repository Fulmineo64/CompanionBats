package dev.fulmineo.companion_bats.mixin;

import java.util.Iterator;

import javax.swing.text.html.parser.Entity;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatEntity;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

	public ServerPlayerEntityMixin(MinecraftServer server, ServerWorld world, GameProfile profile) {
		super(world, world.getSpawnPos(), world.getSpawnAngle(), profile);
	}

	@Inject(at = @At("HEAD"), method = "moveToWorld(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/entity/Entity;")
	public void moveToWorldMixin(ServerWorld destination, CallbackInfoReturnable<Entity> ci) {
		this.recallOwnedBats();
	}

	@Inject(at = @At("HEAD"), method = "onDisconnect()V")
	public void onDisconnectMixin(CallbackInfo info) {
		this.recallOwnedBats();
	}

	public void recallOwnedBats(){
		PlayerInventory inventory = this.getInventory();
		ImmutableList<DefaultedList<ItemStack>> mainAndOffhand = ImmutableList.of(inventory.main, inventory.offHand);
		Iterator<DefaultedList<ItemStack>> iterator = mainAndOffhand.iterator();
		while (iterator.hasNext()) {
			DefaultedList<ItemStack> defaultedList = (DefaultedList<ItemStack>) iterator.next();
			for (int i = 0; i < defaultedList.size(); ++i) {
				if (defaultedList.get(i).getItem() == CompanionBats.BAT_FLUTE_ITEM) {
					CompanionBatEntity entity = (CompanionBatEntity) ((ServerWorld) this.world).getEntity(defaultedList.get(i).getNbt().getUuid("EntityUUID"));
					if (entity != null){
						defaultedList.set(i, entity.toItemStack());
						entity.discard();
					}
				}
			}
		}
	}

}
