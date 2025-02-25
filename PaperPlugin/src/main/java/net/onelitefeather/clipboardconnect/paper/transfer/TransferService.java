package net.onelitefeather.clipboardconnect.paper.transfer;

import com.fastasyncworldedit.core.extent.clipboard.MemoryOptimizedClipboard;
import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import net.onelitefeather.clipboardconnect.api.transfer.TransferContext;
import net.onelitefeather.clipboardconnect.paper.ClipboardConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public final class TransferService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);
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
        }, this.plugin.getExecutorService(actor))
                .thenApplyAsync(session -> {
                    final TransferContext transferContext = TransferContext.create();
                    transferContext.setActor(actor);
                    transferContext.setClipboardFormat(this.plugin.getPluginConfig().builtInClipboardFormat());
                    transferContext.setTransferStrategy(this.plugin.getTransferStrategy());
                    try (final var reader = transferContext.doDownload()) {
                        if (reader == null) {
                            return TransferStatus.EMPTY;
                        }
                        final Clipboard clipboard;
                        // START - WorldEdit Compatibility
                        if (ClipboardConnect.IS_FAST_ASYNC_WORLD_EDIT) {
                            clipboard = reader.read(actor.getUniqueId(), TransferService::createMemoryClipboard);
                        } else {
                            clipboard = reader.read(actor.getUniqueId());
                        }
                        // END - WorldEdit Compatibility
                        session.setClipboard(new ClipboardHolder(clipboard));
                        return TransferStatus.COMPLETED;
                    } catch (final Exception e) {
                        LOGGER.error("Failed to load clipboard", e);
                        throw new IllegalStateException("Failed to load clipboard", e);
                    }
                }, this.plugin.getExecutorService(actor));
    }

    private static Clipboard createMemoryClipboard(final BlockVector3 blockVector3) {
        return new MemoryOptimizedClipboard(new CuboidRegion(
                null,
                BlockVector3.ZERO,
                blockVector3.subtract(BlockVector3.ONE),
                false
        ));
    }

    @SuppressWarnings("deprecation")
    public CompletableFuture<TransferStatus> uploadClipboard(final Actor actor) {
        return CompletableFuture.completedFuture(actor).thenApplyAsync(actor1 -> {
            final var session = WorldEdit.getInstance().getSessionManager().get(actor1);
            if (session == null) {
                throw new IllegalStateException("You must have a WorldEdit session to use this function.");
            }
            return session;
        }, this.plugin.getExecutorService(actor))
                .thenApplyAsync(localSession -> {
                    try {
                        return localSession.getClipboard();
                    } catch (final EmptyClipboardException e) {
                        return null;
                    }
                }, this.plugin.getExecutorService(actor))
                .thenApplyAsync(clipboard -> {
                    if (clipboard == null) {
                        LOGGER.debug("No clipboard found for actor {}", actor.getName());
                        return TransferStatus.EMPTY;
                    }
                    final TransferContext transferContext = TransferContext.create();
                    transferContext.setActor(actor);
                    transferContext.setClipboardFormat(this.plugin.getPluginConfig().builtInClipboardFormat());
                    transferContext.setTransferStrategy(this.plugin.getTransferStrategy());
                    transferContext.uploadBegin();
                    try (final var writer = transferContext.doUpload()) {
                        if (writer == null) {
                            return TransferStatus.EMPTY;
                        }
                        writer.write(clipboard.getClipboard());
                        transferContext.uploadEnd();
                        return TransferStatus.COMPLETED;
                    } catch (final Exception e) {
                        LOGGER.error("Failed to save clipboard", e);
                        throw new IllegalStateException("Failed to save clipboard", e);
                    }
                }, this.plugin.getExecutorService(actor));
    }

}
