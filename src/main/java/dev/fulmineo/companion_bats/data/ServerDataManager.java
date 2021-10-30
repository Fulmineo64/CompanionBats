package dev.fulmineo.companion_bats.data;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.fulmineo.companion_bats.CompanionBats;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

public class ServerDataManager {
	private static final Gson GSON = new GsonBuilder()
    //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .create();

	public static CompanionBatCombatLevel[] combatLevels;
	public static Map<String, CompanionBatClass> classes = new HashMap<>();

	public static void init(){
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return new Identifier(CompanionBats.MOD_ID, "data");
			}

			@Override
			public void reload(ResourceManager manager) {
				classes.clear();

				Identifier combatLevelsId = new Identifier("companion_bats:bat_combat_levels.json");
				if (!manager.containsResource(combatLevelsId)) {
					CompanionBats.info("Missing combat levels");
					return;
				}

				try(InputStream stream = manager.getResource(combatLevelsId).getInputStream()) {
					try {
						combatLevels = GSON.fromJson(new String(stream.readAllBytes()), CompanionBatCombatLevel[].class);
					} catch (Exception e) {
						CompanionBats.info("Couldn't parse combat levels");
					}
				} catch(Exception e) {
					CompanionBats.info("Error occurred while loading resource json "+combatLevelsId.toString());
				}


				for(Identifier id : manager.findResources("bat_classes", path -> path.endsWith(".json"))) {
					try(InputStream stream = manager.getResource(id).getInputStream()) {
						try {
							String className = id.getNamespace() + ":" + id.getPath().replace("bat_classes/", "").replace(".json", "");
							classes.put(className, GSON.fromJson(new String(stream.readAllBytes()), CompanionBatClass.class));
						} catch (Exception e) {
							CompanionBats.info("Couldn't parse class levels "+id.toString());
						}
					} catch(Exception e) {
						CompanionBats.info("Error occurred while loading resource json "+id.toString());
					}
				}
			}
		});
	}

	public static float getLevelHealth(int level) {
		return combatLevels[level].healthBonus;
	}

	public static float getLevelAttack(int level) {
		return combatLevels[level].attackBonus;
	}

	public static float getLevelSpeed(int level) {
		return combatLevels[level].speedBonus;
	}
}
