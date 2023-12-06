package net.onelitefeather.clipboardconnect.command

import net.kyori.adventure.audience.MessageType
import net.kyori.adventure.identity.Identified
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import net.onelitefeather.clipboardconnect.conversation.*
import org.bukkit.entity.Player

/**
 * This class represents a ClipboardPlayer, which is a player that can interact with the clipboard and engage in conversations.
 * It extends the ClipboardSender class and implements the Conversable interface.
 *
 * @property player The player associated with this ClipboardPlayer
 * @constructor Creates a ClipboardPlayer with the given player
 */
class ClipboardPlayer(private val player: Player) : ClipboardSender(player), Conversable {

    private val conversationTracker = ConversationTracker()

    /**
     * Sends a message to the player associated with this ClipboardPlayer.
     *
     * @param source The source of the message
     * @param message The message to be sent
     * @param type The type of the message
     */
    override fun sendMessage(source: Identified, message: Component, type: MessageType) {
        player.sendMessage(source, message, type)
    }

    /**
     * Sends a message to the player associated with this ClipboardPlayer.
     *
     * @param source The source of the message
     * @param message The message to be sent
     * @param type The type of the message
     */
    override fun sendMessage(source: Identity, message: Component, type: MessageType) {
        player.sendMessage(source, message, type)
    }

    /**
     * Returns the player associated with this ClipboardPlayer as the command sender.
     *
     * @return The player command sender.
     */
    override fun getCommandSender(): Player {
        return player
    }

    /**
     * Returns whether the object is actively engaged in a conversation.
     *
     * @return True if a conversation is in progress, false otherwise
     */
    override fun isConversing(): Boolean {
        return this.conversationTracker.isConversing
    }

    /**
     * Accepts input into the active conversation. If no conversation is in
     * progress, this method does nothing.
     *
     * @param input The input message into the conversation
     */
    override fun acceptConversationInput(input: String) {
        this.conversationTracker.acceptConversationInput(input)
    }

    /**
     * Begins a conversation with the specified Conversation.
     *
     * @param conversation The Conversation to begin
     * @return True if the conversation should proceed, false if it has been enqueued
     */
    override fun beginConversation(conversation: Conversation): Boolean {
        return this.conversationTracker.beginConversation(conversation)
    }

    /**
     * Abandons an active conversation.
     *
     * @param conversation The conversation to abandon
     */
    override fun abandonConversation(conversation: Conversation) {
        this.conversationTracker.abandonConversation(conversation, ConversationAbandonedEvent(conversation, ManuallyAbandonedConversationCanceller()))
    }

    /**
     * Abandons an active conversation.
     *
     * @param conversation The conversation to abandon
     * @param details Details about why the conversation was abandoned
     */
    override fun abandonConversation(conversation: Conversation, details: ConversationAbandonedEvent) {
        this.conversationTracker.abandonConversation(conversation, details)
    }

    /**
     * Sets custom suggestions for the player during a conversation.
     *
     * @param completions The collection of completion suggestions
     */
    override fun setCustomSuggestionToPlayer(completions: Collection<String>) {
        if (completions.isNotEmpty()) {
            this.player.setCustomChatCompletions(completions)
        }
    }
}