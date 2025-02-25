package net.onelitefeather.clipboardconnect.strategies;

import com.github.luben.zstd.ZstdInputStream;
import com.github.luben.zstd.ZstdOutputStream;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import net.onelitefeather.clipboardconnect.api.config.PluginConfig;
import net.onelitefeather.clipboardconnect.api.transfer.TransferStrategy;
import org.bukkit.Bukkit;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public final class ZstdRedisTransferStrategy implements TransferStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZstdRedisTransferStrategy.class);
    private static final String UPLOAD_TOPIC = "clipboardconnect:upload";
    private static final List<UUID> UPLOADING = new ArrayList<>();
    private final RedissonClient redisson;
    private final PluginConfig pluginConfig;
    private final RTopic topic;

    public ZstdRedisTransferStrategy(final RedissonClient redisson, PluginConfig pluginConfig, Consumer<Actor> clipboardReady) {
        this.redisson = redisson;
        this.pluginConfig = pluginConfig;
        this.topic = this.redisson.getTopic(UPLOAD_TOPIC);
        this.topic.addListener(RedisTransferStrategy.UploadBegin.class, (channel, message) -> {
            LOGGER.debug("Received upload begin message for actor {}", message);
            UPLOADING.add(message.actorId());
        });
        this.topic.addListener(RedisTransferStrategy.UploadEnd.class, (channel, message) -> {
            LOGGER.debug("Received upload end message for actor {}", message);
            UPLOADING.remove(message.actorId());
            clipboardReady.accept(BukkitAdapter.adapt(Bukkit.getPlayer(message.actorId())));
        });
    }

    @Override
    public void uploadBegin(Actor actor) {
        this.topic.publish(new RedisTransferStrategy.UploadBegin(actor.getUniqueId()));
    }

    @Override
    public void uploadEnd(Actor actor) {
        this.topic.publish(new RedisTransferStrategy.UploadEnd(actor.getUniqueId()));
    }

    @Override
    public boolean isUploading(Actor actor) {
        return UPLOADING.contains(actor.getUniqueId());
    }

    @Override
    public ClipboardWriter doUpload(final Actor actor, final BuiltInClipboardFormat format) {
        LOGGER.debug("Getting stream for actor {}", actor.getUniqueId());
        final var stream = redisson.getBinaryStream(actor.getUniqueId().toString());
        try {
            LOGGER.debug("Setting expire time for stream");
            stream.expire(Duration.ofMinutes(pluginConfig.expireTimeInRedis()));
            LOGGER.debug("Creating writer for stream");
            return format.getWriter(new ZstdOutputStream(stream.getOutputStream()));
        } catch (final IOException e) {
            LOGGER.error("Failed to create writer for Redis stream", e);
        }
        LOGGER.debug("No stream found for actor {}", actor.getUniqueId());
        return null;
    }

    @Override
    public ClipboardReader doDownload(final Actor actor, final BuiltInClipboardFormat format) {
        LOGGER.debug("Getting stream for actor {}", actor.getUniqueId());
        final var stream = redisson.getBinaryStream(actor.getUniqueId().toString());
        LOGGER.debug("Checking if stream exists");
        if (stream.isExists()) {
            try {
                LOGGER.debug("Creating reader for stream");
                return format.getReader(new ZstdInputStream(stream.getInputStream()));
            } catch (final IOException e) {
                LOGGER.error("Failed to create reader for Redis stream", e);
            }
        }
        LOGGER.debug("No stream found for actor {}", actor.getUniqueId());
        return null;
    }
}
