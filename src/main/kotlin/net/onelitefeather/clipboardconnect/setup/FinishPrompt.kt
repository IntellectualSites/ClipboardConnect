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
            MiniMessage.miniMessage().deserialize("<click:COPY_TO_CLIPBOARD:'docker-compose up -d'><green>Setup completed, all settings are now generated! You need to restart your server to get enable the plugin <bold>also run before restart \"docker-compose up -d\" to start the redis instance</bold><gray>(Click the message)</click>")
        } else {
            MiniMessage.miniMessage().deserialize("<green>Setup completed, all settings are now generated! You need to restart your server to get enable the plugin")
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