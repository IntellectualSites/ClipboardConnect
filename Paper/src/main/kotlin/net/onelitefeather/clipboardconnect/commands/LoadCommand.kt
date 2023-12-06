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

@CommandMethod("clipboardconnect|clipcon")
class LoadCommand @Inject constructor(private val syncService: SyncService, @Named("prefix") private val prefix: Component){

    @CommandMethod("load")
    @ProxiedBy("gload")
    @CommandPermission("clipboardconnect.command.load")
    @CommandDescription("Loads a clipboard global")
    fun execute(clipboardPlayer: ClipboardPlayer) {
        if (syncService.syncPull(clipboardPlayer.getWorldEditPlayer())) {
            clipboardPlayer.sendMessage(MiniMessage.miniMessage().deserialize("<prefix><green>Clipboard was successfully loaded", Placeholder.component("prefix",prefix)))
        } else {
            clipboardPlayer.sendMessage(MiniMessage.miniMessage().deserialize("<prefix><red>Clipboard has some issues to load", Placeholder.component("prefix",prefix)))
        }
    }
}