package net.onelitefeather.clipboardconnect.api.config;

import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;

final class PluginConfigBuilder implements PluginConfig.Builder {

    private StrategyModel strategyModel;
    private int cacheTimeForBungeeCordStrategy;
    private BuiltInClipboardFormat builtInClipboardFormat;

    @Override
    public PluginConfig.Builder strategyModel(StrategyModel strategyModel) {
        this.strategyModel = strategyModel;
        return this;
    }

    @Override
    public PluginConfig.Builder expireTimeInRedis(int expireTimeInRedis) {
        this.cacheTimeForBungeeCordStrategy = expireTimeInRedis;
        return this;
    }

    @Override
    public PluginConfig.Builder builtInClipboardFormat(BuiltInClipboardFormat builtInClipboardFormat) {
        this.builtInClipboardFormat = builtInClipboardFormat;
        return this;
    }

    @Override
    public PluginConfig build() {
        return new PluginConfigImpl(strategyModel, cacheTimeForBungeeCordStrategy, builtInClipboardFormat);
    }
}
