package com.redpxnda.nucleus.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.redpxnda.nucleus.Nucleus;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.entity.player.PlayerEntity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.redpxnda.nucleus.Nucleus.LOGGER;

/**
 * {@link SupporterUtil} is the main class used to manage Modding Magnificence supporters.
 */
public class SupporterUtil {
    private static final Map<UUID, Integer> supporters = new HashMap<>();

    public static int getTier(PlayerEntity player) {
        return getTier(player.getUuid());
    }

    public static int getTier(UUID uuid) {
        Integer tier = supporters.get(uuid);
        if (tier != null) return tier;

        tier = getTierViaHttp(uuid);
        supporters.put(uuid, tier);
        return tier;
    }

    public static int getTierViaHttp(UUID uuid) {
        try {
            URL url = new URL("http://54.39.92.171:8084/user/" + (uuid.toString().replace("-", "")));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream stream = connection.getInputStream();
            JsonArray array = Nucleus.GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonArray.class);
            if (array.isEmpty()) return 0;
            JsonElement element = array.get(0);
            if (element instanceof JsonObject object && object.has("tier") && object.get("tier") instanceof JsonPrimitive p && p.isNumber())
                return p.getAsInt();
            else {
                LOGGER.warn("Invalid JSON from supporter data of uuid {}! JSON: {}", uuid, element);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to fetch supporter data from uuid {}!", uuid);
            e.printStackTrace();
        }
        return 0;
    }

    public static void init() {
//        PlayerEvent.PLAYER_JOIN.register(p -> {
//            int t = getTier(p);
//            LOGGER.info("Saved supporter tier data for " + p.getEntityName() + "(UUID: " + p.getUuidAsString() + "). Tier: " + t);
//        });
    }
}
