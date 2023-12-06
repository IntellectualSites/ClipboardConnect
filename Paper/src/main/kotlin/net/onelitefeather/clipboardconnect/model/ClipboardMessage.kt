package net.onelitefeather.clipboardconnect.model

import java.util.UUID

data class ClipboardMessage(
    val userId: UUID,
    val fromServer: String
)
