package net.onelitefeather.clipboardconnect.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.luben.zstd.ZstdInputStream
import com.github.luben.zstd.ZstdOutputStream
import com.sk89q.worldedit.WorldEdit
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
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.codec.JsonJacksonCodec
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
    private val messageRQueue = redisson.getQueue<ClipboardMessage>(topicName, JsonJacksonCodec(jacksonObjectMapper()))
    private val duration: Duration = loadDuration()


    init {
        plugin.server.scheduler.runTaskTimerAsynchronously(plugin, this::pollUpdates, 0, 20)
        messageRQueue.expire(Duration.parse("5m").toJavaDuration())
    }

    private fun pollUpdates() {
        val message = messageRQueue.peek()
        if (message != null) {
            val player = Bukkit.getPlayer(message.userId) ?: return
            if (syncPull(BukkitAdapter.adapt(player))) {
                player.sendMessage(MiniMessage.miniMessage().deserialize("<prefix><green>Clipboard from <gold><server> <green>was successful transfer to this server", Placeholder.unparsed("server", message.fromServer), Placeholder.component("prefix",prefix)))
            }
            messageRQueue.remove(message)
        }
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

    fun syncPush(actor: Actor, automatic: Boolean = false): Boolean {
        val stream = redisson.getBinaryStream(actor.uniqueId.toString())
        if (stream.isExists) {
            stream.delete()
        }
        val session = WorldEdit.getInstance().sessionManager.get(actor)
        val clipboardHolder = session.clipboard ?: return false
        val clipboard = clipboardHolder.clipboard

        BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(ZstdOutputStream(stream.outputStream)).use {
            it.write(clipboard)
            plugin.componentLogger.debug(MiniMessage.miniMessage().deserialize("<green>Clipboard from <actor> was successful written into output stream", Placeholder.unparsed("actor", actor.name)))
        }
        stream.expire(duration.toJavaDuration())
        if (automatic) {
            messageRQueue.add(ClipboardMessage(actor.uniqueId, serverName))
        }
        return true
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
            val session = WorldEdit.getInstance().sessionManager.get(actor)
            BuiltInClipboardFormat.SPONGE_SCHEMATIC.getReader(ZstdInputStream(stream.inputStream)).use {
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