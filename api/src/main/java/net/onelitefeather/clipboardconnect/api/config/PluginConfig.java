package net.onelitefeather.clipboardconnect.api.config;

import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;

public sealed interface PluginConfig permits InternalPluginConfig, PluginConfigImpl {
    String CONFIG_FILE_NAME = "config.json";

    StrategyModel strategyModel();

    BuiltInClipboardFormat builtInClipboardFormat();

    int expireTimeInRedis();

    static Builder builder() {
        return new PluginConfigBuilder();
    }

    static PluginConfig from(PluginConfig config) {
        return builder()
                .strategyModel(config.strategyModel())
                .expireTimeInRedis(config.expireTimeInRedis())
                .builtInClipboardFormat(config.builtInClipboardFormat())
                .build();
    }

    sealed interface Builder permits PluginConfigBuilder {
        Builder strategyModel(StrategyModel strategyModel);
        Builder expireTimeInRedis(int expireTimeInRedis);
        Builder builtInClipboardFormat(BuiltInClipboardFormat builtInClipboardFormat);

        PluginConfig build();
    }
}
