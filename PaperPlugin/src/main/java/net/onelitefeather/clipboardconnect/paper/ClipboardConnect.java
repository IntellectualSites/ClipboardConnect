package net.onelitefeather.clipboardconnect.paper;

import com.fastasyncworldedit.core.util.TaskManager;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Actor;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import net.onelitefeather.clipboardconnect.api.config.PluginConfig;
import net.onelitefeather.clipboardconnect.api.config.PluginConfigProvider;
import net.onelitefeather.clipboardconnect.api.event.ClipboardReadyEvent;
import net.onelitefeather.clipboardconnect.api.transfer.TransferStrategy;
import net.onelitefeather.clipboardconnect.paper.commands.CommandRegistry;
import net.onelitefeather.clipboardconnect.paper.transfer.TransferService;
import net.onelitefeather.clipboardconnect.paper.translations.PluginTranslationRegistry;
import net.onelitefeather.clipboardconnect.strategies.RedisTransferStrategy;
import net.onelitefeather.clipboardconnect.strategies.ZstdRedisTransferStrategy;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

public final class ClipboardConnect extends JavaPlugin {


    public static final Component PREFIX = MiniMessage.miniMessage().deserialize("<gold>[<green>ClipboardConnect<gold>] ");
    public static final boolean isFastAsyncWorldEdit = Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit");
    private PluginConfig pluginConfig;
    private TransferStrategy transferStrategy;
    private TransferService transferService;
    @Override
    public void onLoad() {
        final var dataFolder = this.getDataFolder().toPath();
        if (Files.notExists(dataFolder)) {
            try {
                Files.createDirectories(dataFolder);
            } catch (final Exception e) {
                getSLF4JLogger().error("Failed to create data folder", e);
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
        final PluginConfigProvider pluginConfigProvider = PluginConfigProvider.create(this.getDataFolder().toPath());
        if (this.pluginConfig == null) {
            this.pluginConfig = pluginConfigProvider.getPluginConfig();
        }
        if (Files.notExists(dataFolder.resolve(PluginConfig.CONFIG_FILE_NAME))) {
            pluginConfigProvider.saveConfig(pluginConfig);
        }
        loadStrategy();
    }

    private RedissonClient buildRedisson() {
        final var redisFile = this.getDataFolder().toPath().resolve("redis.yml");
        if (Files.notExists(redisFile)) {
            try {
                Files.createFile(redisFile);
            } catch (final Exception e) {
                getSLF4JLogger().error("Failed to create redis file", e);
                Bukkit.getPluginManager().disablePlugin(this);
            }
        }
        try {
            final var config = Config.fromYAML(redisFile.toFile());
            return Redisson.create(config);
        } catch (final IOException e) {
            getSLF4JLogger().error("Failed to load redis file", e);
            Bukkit.getPluginManager().disablePlugin(this);
        } catch (final Exception e) {
            getSLF4JLogger().error("Failed to create redis client", e);
            Bukkit.getPluginManager().disablePlugin(this);
        }
        throw new UnsupportedOperationException("The redis file had some issues");
    }

    @Override
    public void onEnable() {
        new Metrics(this, 20460);
        registerTranslations();
        this.transferService = new TransferService(this);
        var commandRegistry = new CommandRegistry(this);
        commandRegistry.registerCommands();
    }

    private void registerTranslations() {
        final TranslationRegistry translationRegistry = new PluginTranslationRegistry(
                TranslationRegistry.create(
                        Key.key(
                                "clipboardconnect",
                                "translations"
                        )
                )
        );
        translationRegistry.defaultLocale(Locale.US);
        final Path langFolder = getDataFolder().toPath().resolve("lang");
        if (Files.exists(langFolder)) {
            try {
                try (URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{langFolder.toUri().toURL()})) {
                    Locale.availableLocales().map(locale -> {
                        try {
                            return ResourceBundle.getBundle(
                                    "clipboardconnect",
                                    locale,
                                    urlClassLoader,
                                    UTF8ResourceBundleControl.get()
                            );
                        } catch (Exception e) {
                            return null;
                        }
                    }).filter(Objects::nonNull).forEach(bundle -> {
                        try {
                            translationRegistry.registerAll(bundle.getLocale(), bundle, false);
                        } catch (Exception e) {
                            getSLF4JLogger().error("Failed to register translations for {} because: {}", bundle.getLocale(), e.getMessage());
                            getSLF4JLogger().debug("Failed to register translations", e);
                        }
                    });
                }
            } catch (IOException e) {
                getSLF4JLogger().error("Failed to load translations", e);
            }
        } else {
            Locale.availableLocales().map(locale -> {
                try {
                    return ResourceBundle.getBundle(
                            "clipboardconnect",
                            locale,
                            UTF8ResourceBundleControl.get()
                    );
                } catch (Exception e) {
                    return null;
                }
            }).filter(Objects::nonNull).forEach(bundle -> {
                try {
                    translationRegistry.registerAll(bundle.getLocale(), bundle, false);
                } catch (Exception e) {
                    getSLF4JLogger().error("Failed to register translations for {} because: {}", bundle.getLocale(), e.getMessage());
                    getSLF4JLogger().debug("Failed to register translations", e);
                }
            });
        }
        GlobalTranslator.translator().addSource(translationRegistry);
    }

    @Override
    public void reloadConfig() {
        final var dataFolder = this.getDataFolder().toPath();
        if (Files.notExists(dataFolder)) {
            try {
                Files.createDirectories(dataFolder);
            } catch (final Exception e) {
                getSLF4JLogger().error("Failed to create data folder", e);
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }
        final PluginConfigProvider pluginConfigProvider = PluginConfigProvider.create(this.getDataFolder().toPath());
        this.pluginConfig = pluginConfigProvider.getPluginConfig();
        if (Files.notExists(dataFolder.resolve(PluginConfig.CONFIG_FILE_NAME))) {
            pluginConfigProvider.saveConfig(pluginConfig);
        }
    }

    private void loadStrategy() {
        getSLF4JLogger().debug("Loading strategy model {}", pluginConfig.strategyModel());
        this.transferStrategy = switch (pluginConfig.strategyModel()) {
            case REDIS: yield new RedisTransferStrategy(buildRedisson(), this.pluginConfig, this::clipboardReadyEventCall);
            case ZSTD_REDIS: yield new ZstdRedisTransferStrategy(buildRedisson(), this.pluginConfig, this::clipboardReadyEventCall);
        };
        getSLF4JLogger().info("Loaded strategy model {}", pluginConfig.strategyModel());
    }

    private void clipboardReadyEventCall(Actor actor) {
        WorldEdit.getInstance().getEventBus().post(ClipboardReadyEvent.create(actor));
    }

    public Executor getExecutorService() {
        if (isFastAsyncWorldEdit) {
            return TaskManager.taskManager().getPublicForkJoinPool();
        } else {
            return Bukkit.getScheduler().getMainThreadExecutor(this);
        }
    }

    public TransferService getTransferService() {
        return transferService;
    }

    public TransferStrategy getTransferStrategy() {
        return transferStrategy;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }
}
