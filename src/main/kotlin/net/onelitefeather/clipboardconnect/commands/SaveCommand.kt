package net.onelitefeather.clipboardconnect.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import cloud.commandframework.annotations.ProxiedBy
import jakarta.inject.Inject
import jakarta.inject.Named
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.onelitefeather.clipboardconnect.command.ClipboardPlayer
import net.onelitefeather.clipboardconnect.services.SyncService

/**
 * Represents a command for saving a clipboard globally.
 *
 * @property syncService The SyncService instance used for synchronization.
 * @property prefix The prefix component used for message formatting.
 * @constructor Creates a SaveCommand with the given SyncService and prefix.
 */
@CommandMethod("clipboardconnect|clipcon")
class SaveCommand @Inject constructor(private val syncService: SyncService, @Named("prefix") private val prefix: Component, @Named("fawe") private val faweSupport: Boolean){

    /**
     * Executes the save command for a clipboard player.
     *
     * @param clipboardPlayer The ClipboardPlayer instance to save the clipboard for.
     */
    @CommandMethod("save")
    @ProxiedBy("gsave")
    @CommandPermission("clipboardconnect.command.save")
    @CommandDescription("Saves a clipboard global")
    fun execute(clipboardPlayer: ClipboardPlayer)  {
        val run = Runnable {
            if (syncService.syncPush(clipboardPlayer.getWorldEditPlayer(), false)) {
                clipboardPlayer.sendMessage(MiniMessage.miniMessage().deserialize("<prefix><green>Clipboard was successfully uploaded", Placeholder.component("prefix",prefix)))
            } else {
                clipboardPlayer.sendMessage(MiniMessage.miniMessage().deserialize("<prefix><red>Clipboard has some issues to upload", Placeholder.component("prefix",prefix)))
            }
        }
        if (faweSupport) {
            clipboardPlayer.getWorldEditPlayer().runAsyncIfFree(run)
        } else {
            run.run()
        }
    }
}