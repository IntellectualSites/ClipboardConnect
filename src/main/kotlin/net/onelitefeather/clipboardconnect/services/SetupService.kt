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

/**
 * This class represents a SetupService, which is responsible for managing the setup process
 * for ClipboardConnect. It provides methods to start and manage the setup conversation,
 * find a ClipboardPlayer, remove setup players, and generate the configuration.
 *
 * @property javaPlugin The instance of the ClipboardConnect plugin
 * @property prefix The prefix component for messages
 * @constructor Creates a SetupService with the given ClipboardConnect instance and prefix component
 */
@Singleton
class SetupService @Inject constructor(private val javaPlugin: ClipboardConnect, @Named("prefix") private val prefix: Component) {

    private val clipboardPlayers: MutableList<ClipboardPlayer> = mutableListOf()
    private val redisFileName = "redis.yml"

    /**
     * Starts the setup procedure for the given `ClipboardPlayer` using the specified `Prompt`.
     *
     * @param player The `ClipboardPlayer` initiating the setup.
     * @param prompt The `Prompt` to use for the setup procedure.
     * @return void
     */
    fun startSetup(player: ClipboardPlayer, prompt: Prompt) {
        ConversationFactory(javaPlugin).withFirstPrompt(prompt).withPrefix { prefix }.addConversationAbandonedListener(this::remove).buildConversation(player).begin()
        clipboardPlayers.add(player)
    }

    private fun remove(conversationAbandonedEvent: ConversationAbandonedEvent) {
        this.clipboardPlayers.removeIf {
            it == conversationAbandonedEvent.context.forWhom
        }
    }

    /**
     * Finds the `ClipboardPlayer` associated with the specified `Player`.
     *
     * @param player The `Player` object for which to find the `ClipboardPlayer`.
     * @return The `ClipboardPlayer` associated with the specified `Player`, or null if not found.
     */
    fun findClipboardPlayer(player: Player): ClipboardPlayer? {
        return this.clipboardPlayers.firstOrNull { clipboardPlayer: ClipboardPlayer -> clipboardPlayer.getCommandSender().uniqueId == player.uniqueId }
    }


    /**
     * Removes the setup players from the audience.
     *
     * @param audience The audience from which to remove the setup players.
     * @return True if any setup player was removed from the audience, false otherwise.
     */
    fun removeSetupPlayers(audience: Audience): Boolean {
        return clipboardPlayers.any { clipboardPlayer: ClipboardPlayer -> clipboardPlayer.getCommandSender() == audience }
    }

    /**
     * Generates a configuration file based on the provided conversation context.
     *
     * @param conversationContext The conversation context containing the necessary data.
     */
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