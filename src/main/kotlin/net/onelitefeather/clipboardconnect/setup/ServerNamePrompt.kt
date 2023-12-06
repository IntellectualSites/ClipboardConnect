package net.onelitefeather.clipboardconnect.setup

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.onelitefeather.clipboardconnect.conversation.ConversationContext
import net.onelitefeather.clipboardconnect.conversation.Prompt
import net.onelitefeather.clipboardconnect.conversation.StringPrompt

/**
 * The ServerNamePrompt class is a prompt that asks the user for the name of the server instance in the network.
 * It inherits from the StringPrompt class.
 */
class ServerNamePrompt : StringPrompt() {
    override fun getPromptText(context: ConversationContext): Component {
        return MiniMessage.miniMessage().deserialize("<green>What is the name of the server instance in the network? <gray>(example: <gold>Lobby-1<gray>)")
    }

    override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
        input?.let {
            context.setSessionData(SetupKey.SERVER_NAME, it)
        }
        return DurationPrompt()
    }
}