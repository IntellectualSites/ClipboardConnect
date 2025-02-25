package net.onelitefeather.clipboardconnect.paper.commands;

import net.onelitefeather.clipboardconnect.paper.ClipboardConnect;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.PaperCommandManager;

public final class CommandRegistry {

    private final ClipboardConnect clipboardConnect;
    private final PaperCommandManager<ClipboardConnectSender> commandManager;
    public CommandRegistry(ClipboardConnect clipboardConnect) {
        this.clipboardConnect = clipboardConnect;
        this.commandManager = buildCommandManager();
    }

    public void registerCommands() {
        LoadCommand loadCommand = new LoadCommand(clipboardConnect);
        commandManager.command(commandManager.commandBuilder("clipboardconnect")
                .literal("load")
                .handler(loadCommand::loadClipboard));
        commandManager.command(commandManager.commandBuilder("gload")
                .handler(loadCommand::loadClipboard)
                .build());
        SaveCommand saveCommand = new SaveCommand(clipboardConnect);
        commandManager.command(commandManager.commandBuilder("clipboardconnect")
                .literal("save")
                .handler(saveCommand::saveClipboard));
        commandManager.command(commandManager.commandBuilder("gsave")
                .handler(saveCommand::saveClipboard)
                .build());
    }

    private PaperCommandManager<ClipboardConnectSender> buildCommandManager() {
        return PaperCommandManager.builder(new InternalSenderMapper())
                .executionCoordinator(ExecutionCoordinator.simpleCoordinator())
                .buildOnEnable(clipboardConnect);
    }


}
