package net.onelitefeather.clipboardconnect.setup

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.onelitefeather.clipboardconnect.ClipboardConnect
import net.onelitefeather.clipboardconnect.conversation.ConversationContext
import net.onelitefeather.clipboardconnect.conversation.MessagePrompt
import net.onelitefeather.clipboardconnect.conversation.Prompt

/**
 * This class represents a prompt that displays a message to the user and requires
 * no input. It is a subclass of MessagePrompt.
 */
class FinishPrompt : MessagePrompt() {
    override fun getPromptText(context: ConversationContext): Component {

        return if(context.getSessionData(SetupKey.DOCKER_COMPOSE) != null) {
            Component.translatable("setup.prompt.finish.compose")
        } else {
            Component.translatable("setup.prompt.finish.normal")
        }
    }

    override fun getNextPrompt(context: ConversationContext): Prompt? {
        val plugin = context.plugin
        if (plugin is ClipboardConnect) {
            plugin.generateConfig(context)
        }
        return null
    }
}