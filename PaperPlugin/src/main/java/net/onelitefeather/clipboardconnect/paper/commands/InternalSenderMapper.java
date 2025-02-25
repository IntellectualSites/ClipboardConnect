package net.onelitefeather.clipboardconnect.paper.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.SenderMapper;

final class InternalSenderMapper implements SenderMapper<CommandSourceStack, ClipboardConnectSender> {
    @Override
    public @NonNull ClipboardConnectSender map(@NonNull CommandSourceStack base) {
        return ClipboardConnectSender.of(base, BukkitAdapter.adapt(base.getSender()));
    }

    @Override
    public @NonNull CommandSourceStack reverse(@NonNull ClipboardConnectSender mapped) {
        return mapped.source();
    }
}
