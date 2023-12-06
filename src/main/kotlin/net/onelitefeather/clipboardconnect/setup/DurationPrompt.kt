package net.onelitefeather.clipboardconnect.setup

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.onelitefeather.clipboardconnect.conversation.ConversationContext
import net.onelitefeather.clipboardconnect.conversation.Prompt
import net.onelitefeather.clipboardconnect.conversation.StringPrompt
import kotlin.time.Duration

/**
 * A prompt for getting the duration to keep the clipboards in the Redis memory.
 */
class DurationPrompt : StringPrompt() {
    override fun getPromptText(context: ConversationContext): Component {
        return MiniMessage.miniMessage().deserialize("<green>How long do you want to keep the clipboards in the Redis memory? <gray>(example: <gold>6h = 6 hours<gray>)")
    }

    override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
        input?.let {
            context.setSessionData(SetupKey.DURATION, Duration.parse(input))
        }
        return GenerateDockerComposePrompt()
    }
}