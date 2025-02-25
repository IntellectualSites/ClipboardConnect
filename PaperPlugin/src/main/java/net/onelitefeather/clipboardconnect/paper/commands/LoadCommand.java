package net.onelitefeather.clipboardconnect.paper.commands;

import net.kyori.adventure.text.Component;
import net.onelitefeather.clipboardconnect.paper.ClipboardConnect;
import net.onelitefeather.clipboardconnect.paper.transfer.TransferStatus;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;

import java.util.Objects;

public final class LoadCommand {

    private final ClipboardConnect plugin;

    public LoadCommand(final ClipboardConnect plugin) {
        this.plugin = plugin;
    }

    public void loadClipboard(final @NonNull CommandContext<? extends ClipboardConnectSender> commandContext) {
        final var actor = commandContext.sender().actor();
        this.plugin.getTransferService().downloadClipboard(actor).thenAcceptAsync(transferStatus -> {
            if (Objects.requireNonNull(transferStatus) == TransferStatus.COMPLETED) {
                commandContext.sender().sendMessage(ClipboardConnect.PREFIX.append(Component.text("Clipboard loaded.")));
            }
        }).exceptionallyAsync(throwable -> {
            this.plugin.getSLF4JLogger().error("Failed to load clipboard", throwable);
            commandContext.sender().sendMessage(ClipboardConnect.PREFIX.append(Component.text("Failed to load clipboard.")));
            return null;
        }, this.plugin.getExecutorService());

    }
}
