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
 * This class represents a load command that loads a clipboard global.
 *
 * @param syncService The SyncService instance used for synchronization
 * @param prefix The prefix component for message formatting
 */
@CommandMethod("clipboardconnect|clipcon")
class LoadCommand @Inject constructor(private val syncService: SyncService, @Named("prefix") private val prefix: Component, @Named("fawe") private val faweSupport: Boolean){

    /**
     * Executes the load command to load a clipboard global.
     *
     * @param clipboardPlayer The ClipboardPlayer to execute the command for
     */
    @CommandMethod("load")
    @ProxiedBy("gload")
    @CommandPermission("clipboardconnect.command.load")
    @CommandDescription("Loads a clipboard global")
    fun execute(clipboardPlayer: ClipboardPlayer) {
        val run = Runnable {
            if (syncService.syncPull(clipboardPlayer.getWorldEditPlayer())) {
                clipboardPlayer.sendMessage(Component.translatable("command.clipboard.successfully.loaded").arguments(prefix))
            } else {
                clipboardPlayer.sendMessage(Component.translatable("command.clipboard.failed.to.loaded").arguments(prefix))
            }
        }
        if (faweSupport) {
            clipboardPlayer.getWorldEditPlayer().runAsyncIfFree(run)
        } else {
            run.run()
        }

    }
}