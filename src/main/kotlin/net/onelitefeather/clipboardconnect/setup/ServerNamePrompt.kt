package net.onelitefeather.clipboardconnect.setup

import net.kyori.adventure.text.Component
import net.onelitefeather.clipboardconnect.api.conversation.ConversationContext
import net.onelitefeather.clipboardconnect.api.conversation.Prompt
import net.onelitefeather.clipboardconnect.api.conversation.StringPrompt

/**
 * The ServerNamePrompt class is a prompt that asks the user for the name of the server instance in the network.
 * It inherits from the StringPrompt class.
 */
class ServerNamePrompt : net.onelitefeather.clipboardconnect.api.conversation.StringPrompt() {
    override fun getPromptText(context: net.onelitefeather.clipboardconnect.api.conversation.ConversationContext): Component {
        return Component.translatable("setup.prompt.servername.ask")
    }

    override fun acceptInput(context: net.onelitefeather.clipboardconnect.api.conversation.ConversationContext, input: String?): net.onelitefeather.clipboardconnect.api.conversation.Prompt {
        input?.let {
            context.setSessionData(SetupKey.SERVER_NAME, it)
        }
        return DurationPrompt()
    }
}