package net.onelitefeather.clipboardconnect.api.transfer;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;

public interface TransferStrategy {

    void uploadBegin(Actor actor);

    void uploadEnd(Actor actor);

    boolean isUploading(Actor actor);

    ClipboardWriter doUpload(Actor actor, BuiltInClipboardFormat format);

    ClipboardReader doDownload(Actor actor, BuiltInClipboardFormat format);

}
