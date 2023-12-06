package net.onelitefeather.clipboardconnect.setup

/**
 * Enum class representing different setup keys used in the setup process.
 *
 * @param value The string value associated with the setup key.
 */
enum class SetupKey(val value: String) {
    SERVER_NAME("servername"),
    DURATION("duration"),
    DOCKER_COMPOSE("docker-compose.yml"),
    CONNECTION_ADDRESS("singleServerConfig.address"),
}