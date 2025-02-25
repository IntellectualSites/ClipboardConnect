package net.onelitefeather.clipboardconnect.setup

import kotlin.time.Duration
import net.kyori.adventure.text.Component
import net.onelitefeather.clipboardconnect.api.conversation.ConversationContext
import net.onelitefeather.clipboardconnect.api.conversation.Prompt
import net.onelitefeather.clipboardconnect.api.conversation.StringPrompt

/**
 * A prompt for getting the duration to keep the clipboards in the Redis memory.
 */
class DurationPrompt : net.onelitefeather.clipboardconnect.api.conversation.StringPrompt() {
    override fun getPromptText(context: net.onelitefeather.clipboardconnect.api.conversation.ConversationContext): Component {
        if (context.getSessionData("parse-error") != null) {
            context.allSessionData.remove("parse-error")
            return Component.translatable("setup.prompt.duration.failed")
        }
        return Component.translatable("setup.prompt.duration.ask")
    }

    override fun acceptInput(context: net.onelitefeather.clipboardconnect.api.conversation.ConversationContext, input: String?): net.onelitefeather.clipboardconnect.api.conversation.Prompt {
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