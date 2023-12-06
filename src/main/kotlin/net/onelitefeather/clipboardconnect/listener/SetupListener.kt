package net.onelitefeather.clipboardconnect.listener

import io.papermc.paper.event.player.AsyncChatEvent
import jakarta.inject.Inject
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.onelitefeather.clipboardconnect.services.SetupService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class SetupListener @Inject constructor(private val setupService: SetupService) : Listener {

    @EventHandler
    fun chatListening(event: AsyncChatEvent) {
        event.viewers().removeIf(setupService::removeSetupPlayers)
        val player = setupService.findClipboardPlayer(event.player)?: return
        player.acceptConversationInput(PlainTextComponentSerializer.plainText().serialize(event.message()))
        event.isCancelled = true
    }
}