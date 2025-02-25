package net.onelitefeather.clipboardconnect.api.transfer;

import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;

final class TransferContextImpl implements TransferContext {

    private TransferStrategy strategy;
    private Actor actor;
    private BuiltInClipboardFormat format;

    @Override
    public void setTransferStrategy(TransferStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void setActor(Actor actor) {
        this.actor = actor;
    }

    @Override
    public void setClipboardFormat(BuiltInClipboardFormat format) {
        this.format = format;
    }

    @Override
    public void uploadBegin() {
        if (this.actor == null) {
            return;
        }
        if (this.strategy == null) {
            return;
        }
        this.strategy.uploadBegin(this.actor);
    }

    @Override
    public void uploadEnd() {
        if (this.actor == null) {
            return;
        }
        if (this.strategy == null) {
            return;
        }
        this.strategy.uploadEnd(this.actor);
    }

    @Override
    public boolean isUploading() {
        if (this.actor == null) {
            return false;
        }
        if (this.strategy == null) {
            return false;
        }
        return this.strategy.isUploading(this.actor);
    }

    @Override
    public ClipboardReader doDownload() {
        if (this.actor == null) {
            return null;
        }
        if (this.strategy == null) {
            return null;
        }
        return this.strategy.doDownload(this.actor, this.format);
    }

    @Override
    public ClipboardWriter doUpload() {
        if (this.actor == null) {
            return null;
        }
        if (this.strategy == null) {
            return null;
        }
        return this.strategy.doUpload(this.actor, this.format);
    }
}
