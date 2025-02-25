package net.onelitefeather.clipboardconnect.paper.listener;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.eventbus.Subscribe;
import net.kyori.adventure.text.Component;
import net.onelitefeather.clipboardconnect.api.event.ClipboardReadyEvent;
import net.onelitefeather.clipboardconnect.paper.ClipboardConnect;
import net.onelitefeather.clipboardconnect.paper.transfer.TransferStatus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayerJoinListener implements Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerJoinListener.class);
    private final ClipboardConnect plugin;

    public PlayerJoinListener(ClipboardConnect plugin) {
        this.plugin = plugin;
        WorldEdit.getInstance().getEventBus().register(this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        var actor = BukkitAdapter.adapt(player);
        this.plugin.getTransferService().downloadClipboard(actor).thenAcceptAsync(transferStatus -> {
            if (transferStatus == TransferStatus.COMPLETED) {
                LOGGER.debug("Downloaded clipboard for player {}", player.getName());
                player.sendMessage(ClipboardConnect.PREFIX.append(Component.text("Downloaded clipboard")));
            }
        }).exceptionallyAsync(throwable -> {
            LOGGER.error("Failed to download clipboard for player {}", player.getName(), throwable);
            return null;
        });
    }

    @Subscribe
    public void onClipboardReady(ClipboardReadyEvent event) {
        var actor = event.getActor();
        var player = BukkitAdapter.adapt(actor);
        this.plugin.getTransferService().downloadClipboard(actor).thenAcceptAsync(transferStatus -> {
            if (transferStatus == TransferStatus.COMPLETED) {
                LOGGER.debug("Downloaded clipboard for player {}", actor.getName());
                if (player != null) {
                    player.sendMessage(ClipboardConnect.PREFIX.append(Component.text("Clipboard is now ready and successfully transferred")));
                }
            }
        }).exceptionallyAsync(throwable -> {
            LOGGER.error("Failed to upload clipboard for player {}", actor.getName(), throwable);
            return null;
        });
    }
}
