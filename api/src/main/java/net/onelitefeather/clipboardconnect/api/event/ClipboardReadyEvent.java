package net.onelitefeather.clipboardconnect.api.event;

import com.sk89q.worldedit.event.Event;
import com.sk89q.worldedit.extension.platform.Actor;

public final class ClipboardReadyEvent extends Event {

    private final Actor actor;

    public ClipboardReadyEvent(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return actor;
    }

    public static ClipboardReadyEvent create(Actor actor) {
        return new ClipboardReadyEvent(actor);
    }
}
