package net.onelitefeather.clipboardconnect.api.config;

import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;

record InternalPluginConfig(StrategyModel strategyModel, int expireTimeInRedis, BuiltInClipboardFormat builtInClipboardFormat) implements PluginConfig {

    public static InternalPluginConfig defaultConfig() {
        return Instances.DEFAULT;
    }

    static final class Instances {

        private static final StrategyModel STRATEGY_MODEL = StrategyModel.ZSTD_REDIS;
        private static final int EXPIRE_TIME_IN_REDIS = 5;
        private static final BuiltInClipboardFormat BUILT_IN_CLIPBOARD_FORMAT = BuiltInClipboardFormat.FAST_V3;

        private static final InternalPluginConfig DEFAULT;

        static {
            DEFAULT = new InternalPluginConfig(STRATEGY_MODEL, EXPIRE_TIME_IN_REDIS, BUILT_IN_CLIPBOARD_FORMAT);
        }

        private Instances() {
            throw new UnsupportedOperationException("Instances class cannot be instantiated");
        }
    }
}
