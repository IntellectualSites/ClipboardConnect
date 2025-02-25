package net.onelitefeather.clipboardconnect.paper.transfer;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.onelitefeather.clipboardconnect.api.transfer.TransferContext;
import net.onelitefeather.clipboardconnect.paper.ClipboardConnect;

import java.util.concurrent.CompletableFuture;

public final class TransferService {

    private final ClipboardConnect plugin;

    public TransferService(ClipboardConnect plugin) {
        this.plugin = plugin;
    }

    public CompletableFuture<TransferStatus> downloadClipboard(final Actor actor) {
            return CompletableFuture.completedFuture(actor).thenApplyAsync(actor1 -> {
            final var session = WorldEdit.getInstance().getSessionManager().get(actor1);
            if (session == null) {
                throw new IllegalStateException("You must have a WorldEdit session to use this function.");
            }
            return session;
        }, this.plugin.getExecutorService())
                .thenApplyAsync(session -> {
                    final TransferContext transferContext = TransferContext.create();
                    transferContext.setActor(actor);
                    transferContext.setClipboardFormat(this.plugin.getPluginConfig().builtInClipboardFormat());
                    transferContext.setTransferStrategy(this.plugin.getTransferStrategy());
                    try (final var reader = transferContext.doDownload()) {
                        if (reader == null) {
                            throw new NullPointerException("Failed to load clipboard");
                        }
                        session.setClipboard(new ClipboardHolder(reader.read()));
                        return TransferStatus.COMPLETED;
                    } catch (final Exception e) {
                        this.plugin.getSLF4JLogger().error("Failed to load clipboard", e);
                        throw new IllegalStateException("Failed to load clipboard", e);
                    }
                }, this.plugin.getExecutorService());
    }

    @SuppressWarnings("deprecation")
    public CompletableFuture<TransferStatus> uploadClipboard(final Actor actor) {
        return CompletableFuture.completedFuture(actor).thenApplyAsync(actor1 -> {
            final var session = WorldEdit.getInstance().getSessionManager().get(actor1);
            if (session == null) {
                throw new IllegalStateException("You must have a WorldEdit session to use this function.");
            }
            return session;
        }, this.plugin.getExecutorService())
                .thenApplyAsync(localSession -> {
                    final var clipboard = localSession.getClipboard();
                    if (clipboard == null) {
                        throw new IllegalStateException("You must have a clipboard to use this function.");
                    }
                    return clipboard;
                }, this.plugin.getExecutorService())
                .thenApplyAsync(clipboard -> {
                    final TransferContext transferContext = TransferContext.create();
                    transferContext.setActor(actor);
                    transferContext.setClipboardFormat(this.plugin.getPluginConfig().builtInClipboardFormat());
                    transferContext.setTransferStrategy(this.plugin.getTransferStrategy());
                    transferContext.uploadBegin();
                    try (final var writer = transferContext.doUpload()) {
                        writer.write(clipboard.getClipboard());
                        transferContext.uploadEnd();
                        return TransferStatus.COMPLETED;
                    } catch (final Exception e) {
                        this.plugin.getSLF4JLogger().error("Failed to save clipboard", e);
                        throw new IllegalStateException("Failed to save clipboard", e);
                    }
                }, this.plugin.getExecutorService());
    }

}
