package net.onelitefeather.clipboardconnect.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.*

/**
 * Represents a message that is sent or received through the redis queue.
 *
 * @property userId The unique identifier of the user associated with the message.
 * @property fromServer The identifier of the server from which the message is sent or received.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
data class ClipboardMessage(
    @JsonProperty("userId")
    val userId: UUID,
    @JsonProperty("fromServer")
    val fromServer: String
)
