package net.onelitefeather.clipboardconnect.services

import com.fastasyncworldedit.core.Fawe
import com.github.luben.zstd.ZstdInputStream
import com.github.luben.zstd.ZstdOutputStream
import com.sk89q.worldedit.EmptyClipboardException
import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extension.platform.Actor
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
import com.sk89q.worldedit.session.ClipboardHolder
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.logger.slf4j.ComponentLogger
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.onelitefeather.clipboardconnect.ClipboardConnect
import net.onelitefeather.clipboardconnect.model.ClipboardMessage
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.client.RedisConnectionException
import org.redisson.codec.TypedJsonJacksonCodec
import org.redisson.config.Config
import org.slf4j.MarkerFactory
import java.io.IOException
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.reader
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * This class is responsible for synchronizing clipboards between servers using Redisson as the message queue.
 *
 * @property config The configuration file of the plugin.
 * @property plugin The main plugin instance.
 * @property prefix The prefix component used in the plugin's messages.
 * @property redisson The Redisson client instance used to connect to Redis.
 * @property topicName The name of the Redis topic used for message communication.
 * @property serverName The name of the server.
 * @property messageRQueue The Redisson queue used for message communication.
 * @property duration The duration for which clipboards are stored in Redis.
 */
@Singleton
class SyncService @Inject constructor(private val config: FileConfiguration, private val plugin: ClipboardConnect, @Named("prefix") private val prefix: Component, @Named("fawe") private val faweSupport: Boolean) {

    private val logger = ComponentLogger.logger(javaClass)
    private val redisson: RedissonClient = buildRedis()
    private val topicName = "ClipboardConnect"
    private val topicNameUUID = "ClipboardConnect-UUID"
    private val serverName = config.getString("servername") ?: "Unknown"
    private val codec = TypedJsonJacksonCodec(ClipboardMessage::class.java)
    private val messageRQueue = redisson.getQueue<ClipboardMessage>(topicName,codec)
    private val messageUUIDRQueue = redisson.getQueue<String>(topicNameUUID)
    private val duration: Duration = loadDuration()
    private val pushMarker = MarkerFactory.getMarker("Sync Push")
    private val pullMarker = MarkerFactory.getMarker("Sync Pull")


    init {
        plugin.server.scheduler.runTaskTimerAsynchronously(plugin, this::pollUpdates, 0, 20)
        messageRQueue.expire(Duration.parse("5m").toJavaDuration())
        messageUUIDRQueue.expire(Duration.parse("5m").toJavaDuration())
    }

    private fun pollUpdates() {
        try {
            val message = messageRQueue.peek()
            logger.debug(MiniMessage.miniMessage().deserialize("Pull message queue"))
            if (message != null) {
                logger.debug(MiniMessage.miniMessage().deserialize("Found message"))
                val player = Bukkit.getPlayer(message.userId()) ?: return
                if (messageUUIDRQueue.contains(player.uniqueId.toString())) {
                    return
                }
                logger.debug(MiniMessage.miniMessage().deserialize("Found player"))
                if (!player.hasPermission("clipboardconnect.service.load")) return
                logger.debug(MiniMessage.miniMessage().deserialize("Player permission check ok"))
                messageUUIDRQueue.add(player.uniqueId.toString())
                if (syncPull(BukkitAdapter.adapt(player))) {
                    logger.debug(MiniMessage.miniMessage().deserialize("Pull was successful"))
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<prefix><green>Clipboard from <gold><server> <green>was successfully transfered to this server", Placeholder.unparsed("server", message.fromServer()), Placeholder.component("prefix",prefix)))
                    logger.debug(MiniMessage.miniMessage().deserialize("Remove message from queue"))
                    messageRQueue.remove(message)
                    messageUUIDRQueue.remove(player.uniqueId.toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Bukkit.getPluginManager().disablePlugin(plugin)
        }
    }

    private fun buildRedis(): RedissonClient {
        logger.debug(MiniMessage.miniMessage().deserialize("Build redis config"))
        val redisFile = Path(plugin.dataFolder.toString(), "redis.yml")
        if (Files.exists(redisFile)) {
            try {
                logger.debug(MiniMessage.miniMessage().deserialize("Read redis config"))
                val config = Config.fromYAML(redisFile.reader())
                return Redisson.create(config)
            } catch (e: IOException) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>Failed to load redis.yml"), e)
                plugin.server.pluginManager.disablePlugin(plugin)
            } catch (e: RedisConnectionException) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>Failed to initialize a redis connection"), e)
                plugin.server.pluginManager.disablePlugin(plugin)
            } catch (e: Exception) {
                logger.error(MiniMessage.miniMessage().deserialize("<red>Something went wrong while trying to initialize a redis connection"), e)
                plugin.server.pluginManager.disablePlugin(plugin)
            }
        }
        throw NullPointerException()
    }

    /**
     * Synchronizes and saves the clipboard for a given actor.
     *
     * @param actor The actor whose clipboard needs to be synchronized and saved.
     * @param automatic Flag indicating if the synchronization is automatic or manual. Defaults to false.
     * @return True if the synchronization and save were successful, false otherwise.
     */
    @Suppress("DEPRECATION")
    fun syncPush(actor: Actor, automatic: Boolean = true): Boolean  {
        logger.debug(pushMarker, MiniMessage.miniMessage().deserialize("Open actor<player> stream from redis", Placeholder.unparsed("player", actor.name)))
        val stream = redisson.getBinaryStream(actor.uniqueId.toString())
        if (stream.isExists) {
            plugin.componentLogger.debug(pushMarker, MiniMessage.miniMessage().deserialize("Delete old actor<player> stream from redis", Placeholder.unparsed("player", actor.name)))
            stream.delete()
        }
        try {
            val session = WorldEdit.getInstance().sessionManager.get(actor)
            logger.debug(
                pushMarker,
                MiniMessage.miniMessage()
                    .deserialize("Find actor<player> session", Placeholder.unparsed("player", actor.name))
            )

            val pushAsync = Runnable {
                val clipboardHolder = session.clipboard ?: return@Runnable
                logger.debug(
                    pushMarker,
                    MiniMessage.miniMessage().deserialize(
                        "Found actor<player> clipboard holder",
                        Placeholder.unparsed("player", actor.name)
                    )
                )
                val clipboard = clipboardHolder.clipboard
                logger.debug(
                    pushMarker,
                    MiniMessage.miniMessage()
                        .deserialize("Open actor<player> writer", Placeholder.unparsed("player", actor.name))
                )
                if (clipboard == null) {
                    return@Runnable
                }

                val format = if(faweSupport) {
                    BuiltInClipboardFormat.FAST
                } else {
                    BuiltInClipboardFormat.SPONGE_SCHEMATIC
                }
                logger.debug(
                    pushMarker,
                    MiniMessage.miniMessage().deserialize(
                        "Write actor<player> clipboard into stream",
                        Placeholder.unparsed("player", actor.name)
                    )
                )
                try {
                    format.getWriter(ZstdOutputStream(stream.outputStream)).use {
                        it.write(clipboard)
                        logger.debug(
                            pushMarker,
                            MiniMessage.miniMessage().deserialize(
                                "<green>Clipboard from <actor> was successful written into output stream",
                                Placeholder.unparsed("actor", actor.name)
                            )
                        )
                    }

                } catch (e: Exception) {
                    logger.error(MiniMessage.miniMessage().deserialize(
                        "<green>Something went wrong to write clipboard",
                        Placeholder.unparsed("actor", actor.name)
                    ), e)
                }
                logger.debug(
                    pushMarker,
                    MiniMessage.miniMessage().deserialize(
                        "Set actor<player> clipboard expire to <duration>",
                        Placeholder.unparsed("player", actor.name),
                        Placeholder.unparsed("duration", duration.toString())
                    )
                )
                stream.expire(duration.toJavaDuration())
                if (automatic) {
                    logger.debug(
                        pushMarker,
                        MiniMessage.miniMessage().deserialize(
                            "Write actor<player> clipboard info into queue",
                            Placeholder.unparsed("player", actor.name)
                        )
                    )
                    messageRQueue.add(ClipboardMessage(actor.uniqueId, serverName))
                }
            }
            if (faweSupport) {
                Fawe.instance().getClipboardExecutor().submit(actor.uniqueId, pushAsync)
            } else {
                pushAsync.run()
            }
        } catch (e: EmptyClipboardException) {
            return false
        }
        return true
    }


    /**
     * Synchronizes and pulls the clipboard for a given actor.
     *
     * @param actor The actor whose clipboard needs to be synchronized and pulled.
     * @return True if the synchronization and pull were successful, false otherwise.
     */
    @Suppress("Deprecation")
    fun syncPull(actor: Actor): Boolean {
            logger.debug(pullMarker, MiniMessage.miniMessage().deserialize("Open actor<player> stream from redis to pull", Placeholder.unparsed("player", actor.name)))
            val stream = redisson.getBinaryStream(actor.uniqueId.toString())
            if (stream.isExists) {
                logger.debug(pullMarker,
                    MiniMessage.miniMessage().deserialize(
                        "<green>Clipboard from <actor> was successful downloaded",
                        Placeholder.unparsed("actor", actor.name)
                    )
                )
                logger.debug(pullMarker, MiniMessage.miniMessage().deserialize("Find actor<player> session", Placeholder.unparsed("player", actor.name)))
                val session = WorldEdit.getInstance().sessionManager.get(actor)
                logger.debug(pullMarker, MiniMessage.miniMessage().deserialize("Open actor<player> reader", Placeholder.unparsed("player", actor.name)))
                val format = if(faweSupport) {
                    BuiltInClipboardFormat.FAST
                } else {
                    BuiltInClipboardFormat.SPONGE_SCHEMATIC
                }
                try {
                    format.getReader(ZstdInputStream(stream.inputStream)).use {
                        logger.debug(
                            pullMarker,
                            MiniMessage.miniMessage().deserialize(
                                "<green>Clipboard from <actor> was successful written into a clipboard holder",
                                Placeholder.unparsed("actor", actor.name)
                            )
                        )
                        logger.debug(
                            pullMarker,
                            MiniMessage.miniMessage().deserialize(
                                "Create clipboard holder for actor<player>",
                                Placeholder.unparsed("player", actor.name)
                            )
                        )
                        session.clipboard = ClipboardHolder(it.read())
                    }
                    return true
                } catch (e: Exception) {
                    logger.error(MiniMessage.miniMessage().deserialize(
                        "<green>Something went wrong to load clipboard",
                        Placeholder.unparsed("actor", actor.name)
                    ), e)
                }

            }
            return false
    }

    private fun loadDuration(): Duration {
        return Duration.parse(config.getString("duration") ?: throw NullPointerException("Duration entry"))
    }


}