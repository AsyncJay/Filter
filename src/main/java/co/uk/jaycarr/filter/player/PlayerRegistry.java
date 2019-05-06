package co.uk.jaycarr.filter.player;

import co.uk.jaycarr.filter.FilterPlugin;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class PlayerRegistry {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .create();

    private final Path playersDir;
    private final Map<UUID, PlayerData> playerCache = new ConcurrentHashMap<>();
    private final Object lock = new Object();

    public PlayerRegistry(FilterPlugin plugin) {
        this.playersDir = plugin.getDataFolder().toPath().resolve("players");

        if (!Files.exists(this.playersDir)) {
            try {
                Files.createDirectories(this.playersDir);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to create player directory, disabling...", e);
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }
    }

    public Map<UUID, PlayerData> getCache() {
        return Collections.unmodifiableMap(this.playerCache);
    }

    public PlayerData getData(UUID uuid) {
        return this.playerCache.get(uuid);
    }

    public PlayerData loadData(UUID uuid) {
        return this.playerCache.compute(uuid, (uid, data) -> {
            synchronized (this.lock) {
                if (data != null) {
                    return data;
                }

                Path playerPath = this.getPlayerFile(uuid);

                if (!Files.exists(playerPath)) {
                    return new PlayerData(uuid);
                }

                try (Reader reader = Files.newBufferedReader(playerPath)) {
                    return GSON.fromJson(reader, PlayerData.class);
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Failed to load data for UUID: " + uuid, e);
                }

                return null;
            }
        });
    }

    public void saveData(UUID uuid) {
        synchronized (this.lock) {
            PlayerData data = this.playerCache.get(uuid);

            if (data == null) {
                return;
            }

            Path playerPath = this.getPlayerFile(uuid);

            if (!Files.exists(playerPath)) {
                try {
                    Files.createFile(playerPath);
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.WARNING, "Failed to create data file for UUID: " + uuid, e);
                    return;
                }
            }

            try (Writer writer = Files.newBufferedWriter(playerPath)) {
                GSON.toJson(data, writer);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "Failed to save data for UUID: " + uuid, e);
            }

            this.playerCache.remove(uuid);
        }
    }

    private Path getPlayerFile(UUID uuid) {
        return this.playersDir.resolve(uuid.toString() + ".json");
    }
}