package net.onelitefeather.clipboardconnect.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.*

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
data class ClipboardMessage(
    @JsonProperty("userId")
    val userId: UUID,
    @JsonProperty("fromServer")
    val fromServer: String
)
