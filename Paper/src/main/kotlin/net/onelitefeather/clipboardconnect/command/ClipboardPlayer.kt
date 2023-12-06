package net.onelitefeather.clipboardconnect.command

import net.kyori.adventure.audience.MessageType
import net.kyori.adventure.identity.Identified
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import net.onelitefeather.clipboardconnect.conversation.*
import org.bukkit.entity.Player

class ClipboardPlayer(private val player: Player) : ClipboardSender(player), Conversable {

    private val conversationTracker = ConversationTracker()

    override fun sendMessage(source: Identified, message: Component, type: MessageType) {
        player.sendMessage(source, message, type)
    }

    override fun sendMessage(source: Identity, message: Component, type: MessageType) {
        player.sendMessage(source, message, type)
    }

    override fun getCommandSender(): Player {
        return player
    }

    override fun isConversing(): Boolean {
        return this.conversationTracker.isConversing
    }

    override fun acceptConversationInput(input: String) {
        this.conversationTracker.acceptConversationInput(input)
    }

    override fun beginConversation(conversation: Conversation): Boolean {
        return this.conversationTracker.beginConversation(conversation)
    }

    override fun abandonConversation(conversation: Conversation) {
        this.conversationTracker.abandonConversation(conversation, ConversationAbandonedEvent(conversation, ManuallyAbandonedConversationCanceller()))
    }

    override fun abandonConversation(conversation: Conversation, details: ConversationAbandonedEvent) {
        this.conversationTracker.abandonConversation(conversation, details)
    }

    override fun setCustomSuggestionToPlayer(completions: Collection<String>) {
        if (completions.isNotEmpty()) {
            this.player.setCustomChatCompletions(completions)
        }
    }
}