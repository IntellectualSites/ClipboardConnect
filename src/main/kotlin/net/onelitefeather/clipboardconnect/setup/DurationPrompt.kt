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
        if (context.getSessionData("parse-error") != null) {
            context.allSessionData.remove("parse-error")
            return Component.translatable("setup.prompt.duration.failed")
        }
        return Component.translatable("setup.prompt.duration.ask")
    }

    override fun acceptInput(context: ConversationContext, input: String?): Prompt {
        try {
            input?.let {
                context.setSessionData(SetupKey.DURATION, Duration.parse(input.lowercase()))
            }
        } catch (e: Exception) {
            context.setSessionData("parse-error", null)
            return this
        }

        return GenerateDockerComposePrompt()
    }
}