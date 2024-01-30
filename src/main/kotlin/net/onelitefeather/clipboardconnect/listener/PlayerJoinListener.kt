package net.onelitefeather.clipboardconnect.listener

import com.sk89q.worldedit.bukkit.BukkitAdapter
import jakarta.inject.Inject
import jakarta.inject.Named
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.onelitefeather.clipboardconnect.ClipboardConnect
import net.onelitefeather.clipboardconnect.services.SyncService
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Listener class that handles the 'PlayerJoinEvent' when a player joins the server.
 *
 * @property syncService The SyncService instance used for syncing player data.
 * @property plugin The ClipboardConnect plugin instance.
 */
class PlayerJoinListener
@Inject constructor(
    private val syncService: SyncService,
    private val plugin: ClipboardConnect,
    @Named("prefix") private val prefix: Component,
) : Listener {

    /**
     * Handles the 'PlayerQuitEvent' when a player quits the server.
     *
     * @param event The PlayerQuitEvent object.
     */
    @EventHandler(priority = EventPriority.LOWEST)
    fun playerJoin(event: PlayerJoinEvent) {
        val player = event.player
        plugin.componentLogger.debug(
            MiniMessage.miniMessage()
                .deserialize("<player> is logging in", Placeholder.component("player", player.name()))
        )
        if (!player.hasPermission("clipboardconnect.service.load")) return
        plugin.componentLogger.debug(
            MiniMessage.miniMessage()
                .deserialize("<player> permission check was successful", Placeholder.component("player", player.name()))
        )
        val worldEditPlayer = BukkitAdapter.adapt(player)
        plugin.componentLogger.debug(
            MiniMessage.miniMessage()
                .deserialize("Try to pull clipboard for <player>", Placeholder.component("player", player.name()))
        )
        if (syncService.syncPull(worldEditPlayer)) {
            plugin.componentLogger.debug(
                MiniMessage.miniMessage().deserialize(
                    "<green>Clipboard from <actor> was successful written into actor",
                    Placeholder.unparsed("actor", worldEditPlayer.name)
                )
            )
            player.sendMessage(
                MiniMessage.miniMessage().deserialize(
                    "<prefix><green>Clipboard <green>was successfully transfered to this server",
                    Placeholder.component("prefix", prefix)
                )
            )
        }
    }

}