package net.onelitefeather.clipboardconnect.listener

import com.sk89q.worldedit.bukkit.BukkitAdapter
import jakarta.inject.Inject
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.onelitefeather.clipboardconnect.ClipboardConnect
import net.onelitefeather.clipboardconnect.services.SyncService
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Listener class that handles the 'PlayerQuitEvent' when a player quits the server.
 *
 * @property syncService The SyncService instance used for syncing player data.
 * @property plugin The ClipboardConnect plugin instance.
 */
class PlayerQuitListener
    @Inject constructor(private val syncService: SyncService, private val plugin: ClipboardConnect) : Listener {

    /**
     * Handles the 'PlayerQuitEvent' when a player quits the server.
     *
     * @param event The PlayerQuitEvent object.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun playerQuit(event: PlayerQuitEvent)  {
        val player = event.player
        plugin.componentLogger.debug(MiniMessage.miniMessage().deserialize("<player> is logging out", Placeholder.component("player", player.name())))
        if (!player.hasPermission("clipboardconnect.service.save")) return
        plugin.componentLogger.debug(MiniMessage.miniMessage().deserialize("<player> permission check was successful", Placeholder.component("player", player.name())))
        val worldEditPlayer = BukkitAdapter.adapt(player)
        plugin.componentLogger.debug(MiniMessage.miniMessage().deserialize("Try to push clipboard for <player>", Placeholder.component("player", player.name())))
        if (syncService.syncPush(worldEditPlayer)) {
            plugin.componentLogger.debug(MiniMessage.miniMessage().deserialize("<green>Clipboard from <actor> was successful written into output stream", Placeholder.unparsed("actor", worldEditPlayer.name)))
        }
    }

}