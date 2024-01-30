package net.onelitefeather.clipboardconnect.translations

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.minimessage.Context
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.Tag
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.translation.TranslationRegistry
import java.text.MessageFormat
import java.util.*

class PluginTranslationRegistry(private val backedRegistry: TranslationRegistry) : TranslationRegistry {
    override fun name(): Key {
        return backedRegistry.name()
    }

    override fun translate(key: String, locale: Locale): MessageFormat? {
        return null
    }

    override fun translate(component: TranslatableComponent, locale: Locale): Component? {
        val translationFormat = backedRegistry.translate(component.key(), locale) ?: return null

        val miniMessageString = translationFormat.toPattern()

        val resultingComponent = if (component.arguments().isEmpty()) {
            MiniMessage.miniMessage().deserialize(miniMessageString)
        } else {
            MiniMessage.miniMessage().deserialize(
                miniMessageString,
                ArgumentTag(component.arguments())
            )
        }

        return if (component.children().isEmpty()) {
            resultingComponent
        } else {
            resultingComponent.children(component.children())
        }
    }

    override fun contains(key: String): Boolean {
        return backedRegistry.contains(key)
    }

    override fun defaultLocale(locale: Locale) {
        backedRegistry.defaultLocale(locale)
    }

    override fun register(key: String, locale: Locale, format: MessageFormat) {
        backedRegistry.register(key, locale, format)
    }

    override fun unregister(key: String) {
        backedRegistry.unregister(key)
    }

    private class ArgumentTag(private val argumentComponents: List<ComponentLike>) : TagResolver {

        private companion object {
            private const val NAME: String = "argument"
            private const val NAME_1: String = "arg"
        }

        override fun resolve(name: String, arguments: ArgumentQueue, ctx: Context): Tag {
            val index = arguments.popOr("No argument number provided")
                .asInt().orElseThrow {
                    ctx.newException(
                        "Invalid argument number",
                        arguments
                    )
                }

            if (index < 0 || index >= argumentComponents.size) {
                throw ctx.newException("Invalid argument number", arguments)
            }

            return Tag.inserting(argumentComponents[index])
        }

        override fun has(name: String): Boolean {
            return name == NAME || name == NAME_1;
        }
    }
}