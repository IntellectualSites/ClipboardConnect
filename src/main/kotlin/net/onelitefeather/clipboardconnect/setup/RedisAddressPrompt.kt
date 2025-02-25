package net.onelitefeather.clipboardconnect.setup

import net.kyori.adventure.text.Component
import net.onelitefeather.clipboardconnect.api.conversation.ConversationContext
import net.onelitefeather.clipboardconnect.api.conversation.Prompt
import net.onelitefeather.clipboardconnect.api.conversation.StringPrompt

/**
 * The RedisAddressPrompt class is a prompt that asks the user for the address of the Redis server.
 * It inherits from the StringPrompt class.
 */
class RedisAddressPrompt : net.onelitefeather.clipboardconnect.api.conversation.StringPrompt() {
    override fun getPromptText(context: net.onelitefeather.clipboardconnect.api.conversation.ConversationContext): Component {
        return Component.translatable("setup.prompt.redis.ask")
    }

    override fun acceptInput(context: net.onelitefeather.clipboardconnect.api.conversation.ConversationContext, input: String?): net.onelitefeather.clipboardconnect.api.conversation.Prompt {
        input?.let {
            context.setSessionData(SetupKey.CONNECTION_ADDRESS, it)
        }
        return FinishPrompt()
    }
}