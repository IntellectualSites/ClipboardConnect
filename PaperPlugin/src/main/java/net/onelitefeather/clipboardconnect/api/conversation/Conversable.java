package net.onelitefeather.clipboardconnect.api.conversation;

import java.util.Collection;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

/**
 * The Conversable interface is used to indicate objects that can have
 * conversations.
 */
public interface Conversable extends Audience {

    /**
     * Tests to see of a Conversable object is actively engaged in a
     * conversation.
     *
     * @return True if a conversation is in progress
     */
    public boolean isConversing();

    /**
     * Accepts input into the active conversation. If no conversation is in
     * progress, this method does nothing.
     *
     * @param input The input message into the conversation
     */
    public void acceptConversationInput(@NotNull String input);

    /**
     * Enters into a dialog with a Conversation object.
     *
     * @param conversation The conversation to begin
     * @return True if the conversation should proceed, false if it has been
     *     enqueued
     */
    public boolean beginConversation(@NotNull Conversation conversation);

    /**
     * Abandons an active conversation.
     *
     * @param conversation The conversation to abandon
     */
    public void abandonConversation(@NotNull Conversation conversation);

    /**
     * Abandons an active conversation.
     *
     * @param conversation The conversation to abandon
     * @param details Details about why the conversation was abandoned
     */
    public void abandonConversation(@NotNull Conversation conversation, @NotNull ConversationAbandonedEvent details);

    /**
     * Send custom tab completions to the client
     */
    public void setCustomSuggestionToPlayer(Collection<String> completions);

}
