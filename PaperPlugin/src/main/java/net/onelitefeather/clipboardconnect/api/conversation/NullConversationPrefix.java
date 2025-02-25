package net.onelitefeather.clipboardconnect.api.conversation;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * NullConversationPrefix is a {@link ConversationPrefix} implementation that
 * displays nothing in front of conversation output.
 */
public class NullConversationPrefix implements ConversationPrefix {

    /**
     * Prepends each conversation message with an empty string.
     *
     * @param context Context information about the conversation.
     * @return An empty string.
     */

    @Override
    public @NotNull Component getPrefix(@NotNull ConversationContext context) {
        return Component.empty();
    }
}
