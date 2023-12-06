package net.onelitefeather.clipboardconnect.commands

import cloud.commandframework.annotations.CommandDescription
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.CommandPermission
import jakarta.inject.Inject
import net.onelitefeather.clipboardconnect.command.ClipboardPlayer
import net.onelitefeather.clipboardconnect.services.SetupService
import net.onelitefeather.clipboardconnect.setup.ServerNamePrompt


/**
 * The SetupCommand class represents a command for starting the setup procedure.
 *
 * @param setupService The SetupService instance for handling setup operations.
 */
@CommandMethod("clipboardconnect|clipcon")
class SetupCommand
@Inject constructor(private val setupService: SetupService) {

    /**
     * Executes the setup procedure for the given [ClipboardPlayer].
     *
     * @param clipboardPlayer The [ClipboardPlayer] initiating the setup.
     */
    @CommandMethod("setup")
    @CommandPermission("clipboardconnect.command.setup")
    @CommandDescription("Starts the beginning of setup procedure")
    fun execute(clipboardPlayer: ClipboardPlayer) {
        setupService.startSetup(clipboardPlayer, ServerNamePrompt())
    }

}