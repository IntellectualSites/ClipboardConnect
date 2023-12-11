package net.onelitefeather.clipboardconnect.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import java.util.UUID;

public final class ClipboardMessage {

    @JsonProperty("userId")
    private UUID userId;
    @JsonProperty("fromServer")
    private String fromServer;

    public ClipboardMessage() {
    }

    public ClipboardMessage(
            @JsonProperty("userId")
            UUID userId,
            @JsonProperty("fromServer")
            String fromServer
    ) {
        this.userId = userId;
        this.fromServer = fromServer;
    }

    @JsonProperty("userId")
    public UUID userId() {return userId;}

    @JsonProperty("fromServer")
    public String fromServer() {return fromServer;}

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (ClipboardMessage) obj;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.fromServer, that.fromServer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, fromServer);
    }

    @Override
    public String toString() {
        return "ClipboardMessage[" +
                "userId=" + userId + ", " +
                "fromServer=" + fromServer + ']';
    }


}
