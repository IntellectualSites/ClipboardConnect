package net.onelitefeather.clipboardconnect.listener

import com.sk89q.worldedit.bukkit.BukkitAdapter
import jakarta.inject.Inject
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.onelitefeather.clipboardconnect.ClipboardConnect
import net.onelitefeather.clipboardconnect.services.SyncService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

class PlayerQuitListener
    @Inject constructor(private val syncService: SyncService, private val plugin: ClipboardConnect) : Listener {

    @EventHandler
    fun playerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val worldEditPlayer = BukkitAdapter.adapt(player)
        if (syncService.syncPush(worldEditPlayer)) {
            plugin.componentLogger.debug(MiniMessage.miniMessage().deserialize("<green>Clipboard from <actor> was successful written into output stream", Placeholder.unparsed("actor", worldEditPlayer.name)))
        }
    }

}