package dev.fulmineo.companion_bats.network;

import java.util.Map.Entry;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.data.CompanionBatClass;
import dev.fulmineo.companion_bats.data.CompanionBatCombatLevel;
import dev.fulmineo.companion_bats.data.ServerDataManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerNetworkManager {
	public static void registerClientReceiver() {
		ServerPlayNetworking.registerGlobalReceiver(CompanionBats.REQUEST_CLIENT_DATA_ID, (server, player, handler, buf, responseSender) -> {
			ServerNetworkManager.sendDataToClient(player, ServerNetworkManager.getClientDataNbt());
		});
	}

	public static NbtCompound getClientDataNbt() {
		NbtCompound nbt = new NbtCompound();
		// Combat levels
		NbtList combatLevels = new NbtList();
		for (CompanionBatCombatLevel l: ServerDataManager.combatLevels) {
			combatLevels.add(l.writeNbt(new NbtCompound()));
		}
		nbt.put("combatLevels", combatLevels);
		// Classes
		NbtCompound classes = new NbtCompound();
		for (Entry<String, CompanionBatClass> entry: ServerDataManager.classes.entrySet()) {
			classes.put(entry.getKey(), entry.getValue().writeNbt(new NbtCompound()));
		}
		nbt.put("classes", classes);

		nbt.putFloat("baseHealth", CompanionBats.CONFIG.baseHealth);
		nbt.putFloat("baseAttack", CompanionBats.CONFIG.baseAttack);
		nbt.putFloat("baseSpeed", CompanionBats.CONFIG.baseSpeed);
		nbt.putInt("experiencePieGain", CompanionBats.CONFIG.experiencePieGain);
		return nbt;
	}

	public static void sendDataToClient(ServerPlayerEntity player, NbtCompound nbt) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeNbt(nbt);
		ServerPlayNetworking.send(player, CompanionBats.TRANSFER_CLIENT_DATA_ID, buf);
	}
}
