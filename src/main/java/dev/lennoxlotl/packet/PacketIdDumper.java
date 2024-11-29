package dev.lennoxlotl.packet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.api.ModInitializer;
import net.minecraft.network.NetworkState;
import net.minecraft.network.state.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Simple mod to dump packet id mappings.
 *
 * @author lennoxlotl
 * @since 1.0.0
 */
public class PacketIdDumper implements ModInitializer {
    public static final String MOD_ID = "packet-id-dumper";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<NetworkState.Factory<?, ?>> FACTORIES =
        List.of(
            QueryStates.C2S_FACTORY,
            QueryStates.S2C_FACTORY,
            LoginStates.C2S_FACTORY,
            LoginStates.S2C_FACTORY,
            HandshakeStates.C2S_FACTORY,
            ConfigurationStates.C2S_FACTORY,
            ConfigurationStates.S2C_FACTORY,
            PlayStateFactories.C2S,
            PlayStateFactories.S2C);

    @Override
    public void onInitialize() {
        LOGGER.info("Dumping packet id mappings");
        JsonArray stateArray = new JsonArray();
        FACTORIES.stream()
            .map(this::convertFactory)
            .forEach(stateArray::add);

        try {
            Files.writeString(
                new File("packet_ids.json").toPath(),
                GSON.toJson(stateArray),
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE);
        } catch (IOException e) {
            LOGGER.error("Failed to dump packet ids", e);
        }
    }

    /**
     * Creates a json object containing all meta-data of the given state factory.
     *
     * @param factory State factory
     * @return Json object
     * @since 1.0.0
     */
    private JsonObject convertFactory(NetworkState.Factory<?, ?> factory) {
        JsonObject factoryJson = new JsonObject();
        factoryJson.addProperty("id", factory.phase().getId());
        factoryJson.addProperty("side", factory.side().getName());
        JsonArray packetArray = new JsonArray();
        factory.forEachPacketType((type, id) -> {
            JsonObject packetJson = new JsonObject();
            packetJson.addProperty("name", type.toString());
            packetJson.addProperty("id", id);
            packetArray.add(packetJson);
        });
        factoryJson.add("packets", packetArray);
        return factoryJson;
    }
}