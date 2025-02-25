package net.onelitefeather.clipboardconnect.paper.commands;

import com.sk89q.worldedit.extension.platform.Actor;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.audience.Audience;

public sealed interface ClipboardConnectSender extends Audience permits ClipboardConnectSenderImpl {

    CommandSourceStack source();

    Actor actor();

    static ClipboardConnectSender of(CommandSourceStack source, Actor actor) {
        return new ClipboardConnectSenderImpl(source, actor);
    }
}
