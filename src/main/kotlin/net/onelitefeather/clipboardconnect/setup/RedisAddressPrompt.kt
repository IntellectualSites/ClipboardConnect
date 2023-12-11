package net.onelitefeather.clipboardconnect.setup

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.onelitefeather.clipboardconnect.conversation.ConversationContext
import net.onelitefeather.clipboardconnect.conversation.Prompt
import net.onelitefeather.clipboardconnect.conversation.StringPrompt

/**
 * The RedisAddressPrompt class is a prompt that asks the user for the address of the Redis server.
 * It inherits from the StringPrompt class.
 */
class RedisAddressPrompt : StringPrompt() {
    override fun getPromptText(context: ConversationContext): Component {
        return MiniMessage.miniMessage().deserialize("<green>What is the address of redis ? <gray>(<gold>Example format: \"redis://127.0.0.1:6379\"<gray>)")
    }

    override fun acceptInput(context: ConversationContext, input: String?): Prompt {
        input?.let {
            context.setSessionData(SetupKey.CONNECTION_ADDRESS, it)
        }
        return FinishPrompt()
    }
}