package net.onelitefeather.clipboardconnect.command

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extension.platform.Actor
import org.bukkit.command.CommandSender

@Suppress("DEPRECATION")
open class ClipboardSender(private val commandSender: CommandSender) {
    open fun getCommandSender(): CommandSender {
        return commandSender
    }

    fun getWorldEditPlayer(): Actor {
        return BukkitAdapter.adapt(commandSender)
    }
}