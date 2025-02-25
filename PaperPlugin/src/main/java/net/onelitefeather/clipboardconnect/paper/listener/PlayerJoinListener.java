package net.onelitefeather.clipboardconnect.paper.listener;

import net.onelitefeather.clipboardconnect.paper.ClipboardConnect;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {

    private final ClipboardConnect plugin;

    public PlayerJoinListener(ClipboardConnect plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

    }
}
