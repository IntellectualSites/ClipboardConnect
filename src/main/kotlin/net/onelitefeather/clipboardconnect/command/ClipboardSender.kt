package net.onelitefeather.clipboardconnect.command

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extension.platform.Actor
import org.bukkit.command.CommandSender

/**
 * The ClipboardSender class is responsible for managing the interactions with a CommandSender
 * as it relates to clipboard functionality.
 *
 * @constructor Creates a ClipboardSender instance with a specified CommandSender.
 * @param commandSender The CommandSender associated with the ClipboardSender.
 */
open class ClipboardSender(private val commandSender: CommandSender) {
    /**
     * Retrieves the CommandSender associated with the ClipboardSender.
     *
     * @return The CommandSender associated with the ClipboardSender.
     */
    open fun getCommandSender(): CommandSender {
        return commandSender
    }

    /**
     * Retrieves the WorldEdit actor associated with the ClipboardSender.
     *
     * @return The WorldEdit actor associated with the ClipboardSender.
     */
    fun getWorldEditPlayer(): Actor {
        return BukkitAdapter.adapt(commandSender)
    }
}