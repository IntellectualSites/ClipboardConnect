package net.onelitefeather.clipboardconnect.paper.commands;

import com.sk89q.worldedit.extension.platform.Actor;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

record ClipboardConnectSenderImpl(CommandSourceStack source, Actor actor) implements ClipboardConnectSender {
    @Override
    public void sendMessage(@NotNull final Identity source, @NotNull final Component message, @NotNull final MessageType type) {
        this.source.getSender().sendMessage(source, message, type);
    }
}
