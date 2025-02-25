package net.onelitefeather.clipboardconnect.api.conversation;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * PluginNameConversationPrefix is a {@link ConversationPrefix} implementation
 * that displays the plugin name in front of conversation output.
 */
public class PluginNameConversationPrefix implements ConversationPrefix {

    protected String separator;
    protected TextColor prefixColor;
    protected Plugin plugin;

    private Component cachedPrefix;

    public PluginNameConversationPrefix(@NotNull Plugin plugin) {
        this(plugin, " > ", NamedTextColor.LIGHT_PURPLE);
    }

    public PluginNameConversationPrefix(@NotNull Plugin plugin, @NotNull String separator, @NotNull TextColor prefixColor) {
        this.separator = separator;
        this.prefixColor = prefixColor;
        this.plugin = plugin;

        cachedPrefix = Component.text(plugin.getDescription().getName()).color(prefixColor).append(Component.text(separator)).color(NamedTextColor.WHITE);
    }

    /**
     * Prepends each conversation message with the plugin name.
     *
     * @param context Context information about the conversation.
     * @return An empty string.
     */
    @Override
    @NotNull
    public Component getPrefix(@NotNull ConversationContext context) {
        return cachedPrefix;
    }
}
