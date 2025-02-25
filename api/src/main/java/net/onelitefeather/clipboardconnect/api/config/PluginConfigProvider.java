package net.onelitefeather.clipboardconnect.api.config;

import com.google.gson.Gson;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

public final class PluginConfigProvider {

    private static final Logger LOGGER = Logger.getLogger(PluginConfigProvider.class.getName());

    private final Path path;
    private final Gson gson;
    private PluginConfig pluginConfig;

    private PluginConfigProvider(Path path) {
        this.path = path;
        this.gson = new Gson().newBuilder().setPrettyPrinting().create();
        this.loadConfig();
    }

    private void loadConfig() {
        try(final var is = Files.newBufferedReader(this.path.resolve(PluginConfig.CONFIG_FILE_NAME))) {
            final var config = gson.fromJson(is, PluginConfigImpl.class);
            this.pluginConfig = Optional.ofNullable(config).map(PluginConfig.class::cast).orElse(InternalPluginConfig.defaultConfig());
        } catch (final Exception e) {
            LOGGER.warning("Failed to load config: " + e.getMessage());
            pluginConfig = InternalPluginConfig.defaultConfig();
        }
    }

    public void saveConfig(final PluginConfig config) {
        try(final var os = Files.newBufferedWriter(this.path.resolve(PluginConfig.CONFIG_FILE_NAME))) {
            gson.toJson(config, os);
            this.loadConfig();
        } catch (final Exception e) {
            LOGGER.warning("Failed to save config: " + e.getMessage());
        }
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public static PluginConfigProvider create(final Path path) {
        return new PluginConfigProvider(path);
    }
}
