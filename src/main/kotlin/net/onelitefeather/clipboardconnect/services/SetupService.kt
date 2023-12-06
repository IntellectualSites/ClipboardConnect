package net.onelitefeather.clipboardconnect.services

import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.onelitefeather.clipboardconnect.ClipboardConnect
import net.onelitefeather.clipboardconnect.command.ClipboardPlayer
import net.onelitefeather.clipboardconnect.conversation.ConversationAbandonedEvent
import net.onelitefeather.clipboardconnect.conversation.ConversationContext
import net.onelitefeather.clipboardconnect.conversation.ConversationFactory
import net.onelitefeather.clipboardconnect.conversation.Prompt
import net.onelitefeather.clipboardconnect.setup.SetupKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.nio.file.Files
import kotlin.time.Duration

@Singleton
class SetupService @Inject constructor(private val javaPlugin: ClipboardConnect, @Named("prefix") private val prefix: Component) {

    private val clipboardPlayers: MutableList<ClipboardPlayer> = mutableListOf()
    private val redisFileName = "redis.yml"

    fun startSetup(player: ClipboardPlayer, prompt: Prompt) {
        ConversationFactory(javaPlugin).withFirstPrompt(prompt).withPrefix { prefix }.addConversationAbandonedListener(this::remove).buildConversation(player).begin()
        clipboardPlayers.add(player)
    }

    private fun remove(conversationAbandonedEvent: ConversationAbandonedEvent) {
        this.clipboardPlayers.removeIf {
            it == conversationAbandonedEvent.context.forWhom
        }
    }

    fun findClipboardPlayer(player: Player): ClipboardPlayer? {
        return this.clipboardPlayers.firstOrNull { clipboardPlayer: ClipboardPlayer -> clipboardPlayer.getCommandSender().uniqueId == player.uniqueId }
    }


    fun removeSetupPlayers(audience: Audience): Boolean {
        return clipboardPlayers.any { clipboardPlayer: ClipboardPlayer -> clipboardPlayer.getCommandSender() == audience }
    }

    fun generateConfig(conversationContext: ConversationContext) {
        javaPlugin.saveResource(redisFileName, false)
        if(conversationContext.getSessionData(SetupKey.DOCKER_COMPOSE) != null) {
            javaPlugin.saveResource(SetupKey.DOCKER_COMPOSE.value, false)
        } else {
            val config = YamlConfiguration.loadConfiguration(Files.newBufferedReader(javaPlugin.dataFolder.toPath().resolve(redisFileName), Charsets.UTF_8))
            config.set(SetupKey.CONNECTION_ADDRESS.value, conversationContext.getSessionData(SetupKey.CONNECTION_ADDRESS))
            config.setComments("", listOf("Info: https://github.com/redisson/redisson/wiki/2.-Configuration"))
            config.save(javaPlugin.dataFolder.toPath().resolve(redisFileName).toFile())
        }
        javaPlugin.config.set(SetupKey.SERVER_NAME.value, conversationContext.getSessionData(SetupKey.SERVER_NAME))
        javaPlugin.config.set(SetupKey.DURATION.value, (conversationContext.getSessionData(SetupKey.DURATION) as Duration).toString())
        javaPlugin.saveConfig()
    }
}