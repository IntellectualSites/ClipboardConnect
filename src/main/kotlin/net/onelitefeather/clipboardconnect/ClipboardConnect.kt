package net.onelitefeather.clipboardconnect

import cloud.commandframework.annotations.AnnotationParser
import cloud.commandframework.bukkit.CloudBukkitCapabilities
import cloud.commandframework.execution.CommandExecutionCoordinator
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
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import net.kyori.adventure.util.UTF8ResourceBundleControl
import net.onelitefeather.clipboardconnect.command.ClipboardPlayer
import net.onelitefeather.clipboardconnect.command.ClipboardSender
import net.onelitefeather.clipboardconnect.commands.LoadCommand
import net.onelitefeather.clipboardconnect.commands.SaveCommand
import net.onelitefeather.clipboardconnect.commands.SetupCommand
import net.onelitefeather.clipboardconnect.conversation.ConversationContext
import net.onelitefeather.clipboardconnect.listener.PlayerJoinListener
import net.onelitefeather.clipboardconnect.listener.PlayerQuitListener
import net.onelitefeather.clipboardconnect.listener.SetupListener
import net.onelitefeather.clipboardconnect.services.SetupService
import net.onelitefeather.clipboardconnect.translations.PluginTranslationRegistry
import net.onelitefeather.clipboardconnect.utils.RawTypeMatcher
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.IOException
import java.net.URLClassLoader
import java.nio.file.Files
import java.util.*
import kotlin.io.path.Path

/**
 * This class represents a ClipboardConnect plugin that provides functionality
 * for managing the clipboard and setup process. It extends the JavaPlugin class
 * and implements various methods for plugin initialization and configuration.
 *
 */
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
            .bind(FileConfiguration::class.java)
            .toInstance(config))
            .install(BindingBuilder.create()
            .bind(Element.forType(Component::class.java).requireAnnotation(Qualifiers.named("prefix"))).toInstance(prefixComponent))
            .install(BindingBuilder.create().bind(Element
                .forType(Boolean::class.java)
                .requireAnnotation(Qualifiers.named("fawe"))
            ).toInstance(Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null))
            .install(BindingBuilder.create()
                .bindAll(ClipboardConnect::class.java)
                .scoped(Scopes.SINGLETON)
                .toInstance(this))
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
    }

    @Inject
    private fun registerTranslations() {
        val translationRegistry: TranslationRegistry = PluginTranslationRegistry(
            TranslationRegistry.create(
                Key.key(
                    "clipboardconnect",
                    "translations"
                )
            )
        )
        translationRegistry.defaultLocale(Locale.US)
        val langFolder = dataFolder.toPath().resolve("lang")
        if (Files.exists(langFolder)) {
            try {
                URLClassLoader(arrayOf(langFolder.toUri().toURL())).use { urlClassLoader ->
                    config.getStringList("translations").stream().map { languageTag: String ->
                        Locale.forLanguageTag(
                            languageTag
                        )
                    }.forEach { r: Locale ->
                        val bundle = ResourceBundle.getBundle(
                            "clipboardconnect",
                            r,
                            urlClassLoader,
                            UTF8ResourceBundleControl.get()
                        )
                        translationRegistry.registerAll(r, bundle, false)
                    }
                }
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        } else {
            config.getStringList("translations").stream().map { languageTag: String ->
                Locale.forLanguageTag(
                    languageTag
                )
            }.forEach { r: Locale ->
                val bundle =
                    ResourceBundle.getBundle("clipboardconnect", r, UTF8ResourceBundleControl.get())
                translationRegistry.registerAll(r, bundle, false)
            }
        }
        GlobalTranslator.translator().addSource(translationRegistry)
    }

    @Inject
    private fun bsStats() {
        Metrics(this, 20460)
    }

    /**
     * Initializes the command manager for the ClipboardConnect plugin.
     *
     * @param injector The Injector instance used for dependency injection.
     */
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

    /**
     * Parses the given command annotations and creates an instance of AnnotationParser.
     *
     * @param commandManager The PaperCommandManager instance used for command registration.
     * @param injector The Injector instance used for dependency injection.
     */
    @Inject
    @Order(100)
    private fun annotationParser(commandManager: PaperCommandManager<ClipboardSender>, injector: Injector) {
        val annotationParser = AnnotationParser(
            commandManager,
            ClipboardSender::class.java
        )
        injector.install(BindingBuilder.create().bind(Element.forType(annotationParser.javaClass)).bindMatching(RawTypeMatcher.create(annotationParser.javaClass)).toInstance(annotationParser))
    }

    /**
     * Registers event listeners and parses command annotations.
     *
     * @param injector The Injector instance used for dependency injection.
     */
    @Inject
    @Order(150)
    private fun register(injector: Injector) {
        server.pluginManager.registerEvents(injector.instance(SetupListener::class.java), this)
        injector.instance(AnnotationParser::class.java).parse(injector.instance(SetupCommand::class.java))
        val redisFile = Path(dataFolder.toString(), "redis.yml")
        if (Files.exists(redisFile)) {
            server.pluginManager.registerEvents(injector.instance(PlayerQuitListener::class.java), this)
            server.pluginManager.registerEvents(injector.instance(PlayerJoinListener::class.java), this)
            injector.instance(AnnotationParser::class.java).parse(injector.instance(SaveCommand::class.java))
            injector.instance(AnnotationParser::class.java).parse(injector.instance(LoadCommand::class.java))
        } else {
            componentLogger.info(MiniMessage.miniMessage().deserialize("<green>Please run \"/clipboardconnect setup\" ingame"))
        }
    }


    private fun commandSenderChoose(sender: CommandSender): ClipboardSender {
        return if(sender is Player) {
            ClipboardPlayer(sender)
        } else {
            ClipboardSender(sender)
        }
    }

    /**
     * Generates a configuration file based on the provided conversation context.
     *
     * @param conversationContext The conversation context containing the necessary data.
     */
    fun generateConfig(conversationContext: ConversationContext) {
        setupService.generateConfig(conversationContext)
    }
}