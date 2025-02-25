package net.onelitefeather.clipboardconnect.api.config;

import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;

record PluginConfigImpl(StrategyModel strategyModel, int expireTimeInRedis, BuiltInClipboardFormat builtInClipboardFormat) implements PluginConfig {

}
