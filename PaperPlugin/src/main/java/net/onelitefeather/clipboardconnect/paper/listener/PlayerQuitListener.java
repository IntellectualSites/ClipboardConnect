package net.onelitefeather.clipboardconnect.paper.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import net.onelitefeather.clipboardconnect.paper.ClipboardConnect;
import net.onelitefeather.clipboardconnect.paper.transfer.TransferStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayerQuitListener implements Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerQuitListener.class);
    private final ClipboardConnect plugin;

    public PlayerQuitListener(ClipboardConnect plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        var player = event.getPlayer();
        var actor = BukkitAdapter.adapt(player);
        this.plugin.getTransferService().uploadClipboard(actor)
                .thenAcceptAsync(transferStatus -> {
                    if (transferStatus == TransferStatus.COMPLETED) {
                        LOGGER.info("Uploaded clipboard for player {}", player.getName());
                    }
                })
                .exceptionallyAsync(throwable -> {
            LOGGER.error("Failed to upload clipboard for player {}", player.getName(), throwable);
            return null;
        });
    }
}
