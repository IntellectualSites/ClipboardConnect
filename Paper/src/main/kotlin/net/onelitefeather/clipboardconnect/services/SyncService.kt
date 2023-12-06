package net.onelitefeather.clipboardconnect.services

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.extension.platform.Actor
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat
import com.sk89q.worldedit.session.ClipboardHolder
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.onelitefeather.clipboardconnect.ClipboardConnect
import net.onelitefeather.clipboardconnect.model.ClipboardMessage
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.api.listener.MessageListener
import org.redisson.config.Config
import java.io.IOException
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.reader
import kotlin.time.Duration
import kotlin.time.toJavaDuration

@Singleton
class SyncService @Inject constructor(private val config: FileConfiguration, private val plugin: ClipboardConnect, @Named("prefix") private val prefix: Component) {

    private val redisson: RedissonClient = buildRedis()
    private val topicName = "ClipboardConnect"
    private val serverName = config.getString("servername") ?: "Unknown"
    private val sharedTopic = redisson.getShardedTopic(topicName)
    private val duration: Duration = loadDuration()


    init {
        sharedTopic.addListener(ClipboardMessage::class.java, MessageListener { channel, msg ->
           val player = Bukkit.getPlayer(msg.userId) ?: return@MessageListener
            if (syncPull(BukkitAdapter.adapt(player))) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<prefix><green>Clipboard from <server> was successful transfer to this server", Placeholder.unparsed("server", msg.fromServer), Placeholder.component("prefix",prefix)))
            }
        })
    }

    private fun buildRedis(): RedissonClient {
        val redisFile = Path(plugin.dataFolder.toString(), "redis.yml")
        if (Files.exists(redisFile)) {
            try {
                val config = Config.fromYAML(redisFile.reader())
                return Redisson.create(config)
            } catch (e: IOException) {
                plugin.componentLogger.error(MiniMessage.miniMessage().deserialize("<red>Failed to load redis.yml"), e)
                plugin.server.pluginManager.disablePlugin(plugin)
            }
        }
        throw NullPointerException()
    }

    fun syncPush(actor: Actor): Boolean {
        val stream = redisson.getBinaryStream(actor.uniqueId.toString())
        if (stream.isExists) {
            stream.delete()
        }
        val session = actor.session
        val clipboardHolder = session.existingClipboard ?: return false
        val clipboard = clipboardHolder.clipboard
        BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(stream.outputStream).use {
            it.write(clipboard)
            plugin.componentLogger.debug(MiniMessage.miniMessage().deserialize("<green>Clipboard from <actor> was successful written into output stream", Placeholder.unparsed("actor", actor.name)))
            stream.expire(duration.toJavaDuration())
            sharedTopic.publish(ClipboardMessage(actor.uniqueId, serverName))
            return true
        }

    }


    fun syncPull(actor: Actor): Boolean {
        val stream = redisson.getBinaryStream(actor.uniqueId.toString())
        if (stream.isExists) {
            plugin.componentLogger.debug(
                MiniMessage.miniMessage().deserialize(
                    "<green>Clipboard from <actor> was successful downloaded",
                    Placeholder.unparsed("actor", actor.name)
                )
            )
            val session = actor.session
            BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(stream.inputStream).use {
                plugin.componentLogger.debug(
                    MiniMessage.miniMessage().deserialize(
                        "<green>Clipboard from <actor> was successful written into a clipboard holder",
                        Placeholder.unparsed("actor", actor.name)
                    )
                )
                session.clipboard = ClipboardHolder(it.read())
                return true
            }
        }
        return false
    }

    private fun loadDuration(): Duration {
        return Duration.parse(config.getString("duration") ?: throw NullPointerException("Duration entry"))
    }


}