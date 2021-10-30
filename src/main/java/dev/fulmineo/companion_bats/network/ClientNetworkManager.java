package dev.fulmineo.companion_bats.network;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.data.ClientDataManager;
import dev.fulmineo.companion_bats.data.CompanionBatClass;
import dev.fulmineo.companion_bats.data.CompanionBatCombatLevel;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class ClientNetworkManager {
	public static void registerServerReceiver() {
		ClientPlayNetworking.registerGlobalReceiver(CompanionBats.TRANSFER_CLIENT_DATA_ID, (client, handler, buf, responseSender) -> {
			NbtCompound nbt = buf.readNbt();
			// Combat level
			NbtList list = nbt.getList("combatLevels", NbtElement.COMPOUND_TYPE);
			ClientDataManager.combatLevels = new CompanionBatCombatLevel[list.size()];
			for (int i = 0; i < list.size(); i++) {
				ClientDataManager.combatLevels[i] = CompanionBatCombatLevel.fromNbt((NbtCompound)list.get(i));
			}
			// Classes
			ClientDataManager.classes.clear();
			NbtCompound classes = nbt.getCompound("classes");
			for (String key: classes.getKeys()) {
				ClientDataManager.classes.put(key, CompanionBatClass.fromNbt((NbtCompound)classes.get(key)));
			}
			ClientDataManager.baseHealth = nbt.getFloat("baseHealth");
			ClientDataManager.baseAttack = nbt.getFloat("baseAttack");
			ClientDataManager.baseSpeed = nbt.getFloat("baseSpeed");
			ClientDataManager.experiencePieGain = nbt.getInt("experiencePieGain");
		});
	}

	public static void requestClientData() {
		ClientPlayNetworking.send(CompanionBats.REQUEST_CLIENT_DATA_ID, PacketByteBufs.create());
	}
}
