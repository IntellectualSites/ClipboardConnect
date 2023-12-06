package net.onelitefeather.clipboardconnect

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.arguments.parser.StandardParameters
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.meta.CommandMeta
import cloud.commandframework.paper.PaperCommandManager
import dev.derklaro.aerogel.Element
import dev.derklaro.aerogel.Injector
import dev.derklaro.aerogel.Order
import dev.derklaro.aerogel.binding.BindingBuilder
import dev.derklaro.aerogel.member.InjectionSetting
import dev.derklaro.aerogel.util.Qualifiers
import dev.derklaro.aerogel.util.Scopes
import jakarta.inject.Inject
import jakarta.inject.Singleton
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.onelitefeather.clipboardconnect.command.ClipboardPlayer
import net.onelitefeather.clipboardconnect.command.ClipboardSender
import net.onelitefeather.clipboardconnect.commands.SetupCommand
import net.onelitefeather.clipboardconnect.conversation.ConversationContext
import net.onelitefeather.clipboardconnect.listener.PlayerQuitListener
import net.onelitefeather.clipboardconnect.listener.SetupListener
import net.onelitefeather.clipboardconnect.services.SetupService
import net.onelitefeather.clipboardconnect.utils.RawTypeMatcher
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.nio.file.Files
import kotlin.io.path.Path

@Singleton
class ClipboardConnect : JavaPlugin() {

    private val prefixComponent = MiniMessage.miniMessage().deserialize("<gold>[<green>ClipboardConnect<gold>] ")

    @Inject
    private lateinit var setupService: SetupService
    override fun onLoad() {
        saveDefaultConfig()
    }



    override fun onEnable() {
        val injector = Injector.newInjector()
        injector.install(BindingBuilder.create()
            .toInstance(config))
            .install(BindingBuilder.create()
            .bind(Element
                .forType(Component::class.java)
                .requireAnnotation(Qualifiers.named("prefix"))
            ).toInstance(prefixComponent))
            .install(BindingBuilder.create().bindAll(ClipboardConnect::class.java).scoped(Scopes.SINGLETON).toInstance(this))
        injector.memberInjector(ClipboardConnect::class.java).inject(injector.instance(ClipboardConnect::class.java), InjectionSetting.toFlag(
            InjectionSetting.PRIVATE_METHODS,
            InjectionSetting.STATIC_METHODS,
            InjectionSetting.INHERITED_METHODS,
            InjectionSetting.PRIVATE_FIELDS,
            InjectionSetting.STATIC_FIELDS,
            InjectionSetting.INSTANCE_FIELDS,
            InjectionSetting.INHERITED_FIELDS,
            InjectionSetting.RUN_POST_CONSTRUCT_LISTENERS
        ))
        val redisFile = Path(dataFolder.toString(), "redis.yml")
        if (Files.exists(redisFile)) {
            server.pluginManager.registerEvents(injector.instance(PlayerQuitListener::class.java), this)
        } else {
            componentLogger.info(MiniMessage.miniMessage().deserialize("<green>Please run \"/clipboardconnect setup\" ingame"))
        }


    }

    @Inject
    @Order(10)
    private fun commandManager(injector: Injector) {
        val commandManager = PaperCommandManager(this, CommandExecutionCoordinator.simpleCoordinator(), this::commandSenderChoose, ClipboardSender::getCommandSender)
        if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions()
        }

        if (commandManager.hasCapability(CloudBukkitCapabilities.BRIGADIER)) {
            commandManager.registerBrigadier()
        }
        injector.install(BindingBuilder.create().bind(Element.forType(commandManager.javaClass)).bindMatching(RawTypeMatcher.create(commandManager.javaClass)).toInstance(commandManager))
    }

    @Inject
    @Order(100)
    private fun annotationParser(commandManager: PaperCommandManager<ClipboardSender>, injector: Injector) {
        val annotationParser = AnnotationParser(
            commandManager,
            ClipboardSender::class.java
        ) { t ->
            CommandMeta.simple().with(CommandMeta.DESCRIPTION, t.get(StandardParameters.DESCRIPTION, "No description"))
                .build()
        }
        injector.install(BindingBuilder.create().bind(Element.forType(annotationParser.javaClass)).bindMatching(RawTypeMatcher.create(annotationParser.javaClass)).toInstance(annotationParser))
    }

    @Inject
    @Order(150)
    private fun register(injector: Injector) {
        server.pluginManager.registerEvents(injector.instance(SetupListener::class.java), this)
        injector.instance(AnnotationParser::class.java).parse(injector.instance(SetupCommand::class.java))
    }


    private fun commandSenderChoose(sender: CommandSender): ClipboardSender {
        return if(sender is Player) {
            ClipboardPlayer(sender)
        } else {
            ClipboardSender(sender)
        }
    }

    fun generateConfig(conversationContext: ConversationContext) {
        setupService.generateConfig(conversationContext)
    }
}