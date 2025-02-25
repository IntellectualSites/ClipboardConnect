package net.onelitefeather.clipboardconnect.paper.translations;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.translation.TranslationRegistry;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

public class PluginTranslationRegistry implements TranslationRegistry {
    private final TranslationRegistry backedRegistry;

    public PluginTranslationRegistry(TranslationRegistry backedRegistry) {
        this.backedRegistry = backedRegistry;
    }

    @Override
    public Key name() {
        return backedRegistry.name();
    }

    @Override
    public MessageFormat translate(String key, Locale locale) {
        return null;
    }

    @Override
    public Component translate(TranslatableComponent component, Locale locale) {
        MessageFormat translationFormat = backedRegistry.translate(component.key(), locale);
        if (translationFormat == null) {
            return null;
        }

        String miniMessageString = translationFormat.toPattern();
        Component resultingComponent;

        if (component.arguments().isEmpty()) {
            resultingComponent = MiniMessage.miniMessage().deserialize(miniMessageString);
        } else {
            resultingComponent = MiniMessage.miniMessage().deserialize(
                    miniMessageString,
                    new ArgumentTag(component.args())
            );
        }

        if (component.children().isEmpty()) {
            return resultingComponent;
        } else {
            return resultingComponent.children(component.children());
        }
    }

    @Override
    public boolean contains(String key) {
        return backedRegistry.contains(key);
    }

    @Override
    public void defaultLocale(Locale locale) {
        backedRegistry.defaultLocale(locale);
    }

    @Override
    public void register(String key, Locale locale, MessageFormat format) {
        backedRegistry.register(key, locale, format);
    }

    @Override
    public void unregister(String key) {
        backedRegistry.unregister(key);
    }

    private static class ArgumentTag implements TagResolver {
        private static final String NAME = "argument";
        private static final String NAME_1 = "arg";
        private final List<Component> argumentComponents;

        public ArgumentTag(@NotNull List<Component> argumentComponents) {
            this.argumentComponents = argumentComponents;
        }

        @Override
        public Tag resolve(String name, ArgumentQueue arguments, Context ctx) {
            int index = arguments.popOr("No argument number provided")
                    .asInt().orElseThrow(() -> ctx.newException("Invalid argument number", arguments));

            if (index < 0 || index >= argumentComponents.size()) {
                throw ctx.newException("Invalid argument number", arguments);
            }

            return Tag.inserting(argumentComponents.get(index));
        }

        @Override
        public boolean has(String name) {
            return NAME.equals(name) || NAME_1.equals(name);
        }
    }
}
