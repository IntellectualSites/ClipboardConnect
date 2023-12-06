package net.onelitefeather.clipboardconnect.setup

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.onelitefeather.clipboardconnect.conversation.BooleanPrompt
import net.onelitefeather.clipboardconnect.conversation.ConversationContext
import net.onelitefeather.clipboardconnect.conversation.Prompt

class GenerateDockerComposePrompt : BooleanPrompt() {
    override fun getPromptText(context: ConversationContext): Component {
        return MiniMessage.miniMessage().deserialize("<green>Do you need a generated Docker Compose File or do you know your way around redis yourself? <gray>(Write: <gold>Yes for generate a file inside of plugin folder<gray>)")
    }

    override fun acceptValidatedInput(context: ConversationContext, input: Boolean): Prompt? {
        return if (input) {
            context.setSessionData(SetupKey.DOCKER_COMPOSE, input)
            FinishPrompt()
        } else {
            RedisAddressPrompt()
        }
    }
}