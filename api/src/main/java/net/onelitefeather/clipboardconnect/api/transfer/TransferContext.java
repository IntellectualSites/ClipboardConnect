package net.onelitefeather.clipboardconnect.api.transfer;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;

public sealed interface TransferContext permits TransferContextImpl {

    void setTransferStrategy(TransferStrategy strategy);

    void setActor(Actor actor);

    void setClipboardFormat(BuiltInClipboardFormat format);

    void uploadBegin();

    void uploadEnd();

    boolean isUploading();

    ClipboardReader doDownload();

    ClipboardWriter doUpload();

    static TransferContext create() {
        return new TransferContextImpl();
    }
}
