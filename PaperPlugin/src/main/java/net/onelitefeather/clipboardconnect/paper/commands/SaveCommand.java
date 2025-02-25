package net.onelitefeather.clipboardconnect.paper.commands;

import net.kyori.adventure.text.Component;
import net.onelitefeather.clipboardconnect.paper.ClipboardConnect;
import net.onelitefeather.clipboardconnect.paper.transfer.TransferStatus;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class SaveCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(SaveCommand.class);

    private final ClipboardConnect plugin;

    public SaveCommand(final ClipboardConnect plugin) {
        this.plugin = plugin;
    }

    public void saveClipboard(final @NonNull CommandContext<? extends ClipboardConnectSender> commandContext) {
        final var actor = commandContext.sender().actor();
        this.plugin.getTransferService().uploadClipboard(actor)
                .thenAccept(transferStatus -> {
                    if (Objects.requireNonNull(transferStatus) == TransferStatus.COMPLETED) {
                        commandContext.sender().sendMessage(ClipboardConnect.PREFIX.append(Component.text("Clipboard saved.")));
                    }
                }).exceptionallyAsync(throwable -> {
                    LOGGER.error("Failed to save clipboard", throwable);
                    commandContext.sender().sendMessage(ClipboardConnect.PREFIX.append(Component.text("Failed to save clipboard.")));
                    return null;
                }, this.plugin.getExecutorService());
    }
}
