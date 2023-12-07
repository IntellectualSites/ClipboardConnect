package net.onelitefeather.clipboardconnect.listener

import io.papermc.paper.event.player.AsyncChatEvent
import jakarta.inject.Inject
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.onelitefeather.clipboardconnect.ClipboardConnect
import net.onelitefeather.clipboardconnect.services.SetupService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * This class represents a SetupListener, which is responsible for listening to chat events and performing setup-related actions.
 *
 * @param setupService The SetupService instance used for setup operations.
 */
class SetupListener @Inject constructor(private val setupService: SetupService, private val clipboardConnect: ClipboardConnect) : Listener {

    /**
     * Listens to chat events and performs setup-related actions.
     *
     * @param event The AsyncChatEvent to be handled
     */
    @EventHandler
    fun chatListening(event: AsyncChatEvent) {
        val bukkitPlayer = event.player
        val message = event.message()
        clipboardConnect.componentLogger.debug(MiniMessage.miniMessage().deserialize("<player> write following message for setup: <message>", Placeholder.component("player", bukkitPlayer.name()), Placeholder.component("message", message)))
        event.viewers().removeIf(setupService::removeSetupPlayers)
        clipboardConnect.componentLogger.debug(MiniMessage.miniMessage().deserialize("Try to find <player> in setup cache", Placeholder.component("player", bukkitPlayer.name())))
        val player = setupService.findClipboardPlayer(event.player)?: return
        clipboardConnect.componentLogger.debug(MiniMessage.miniMessage().deserialize("Accept message from <player> for setup", Placeholder.component("player", bukkitPlayer.name())))
        player.acceptConversationInput(PlainTextComponentSerializer.plainText().serialize(message))
        event.isCancelled = true
    }
}