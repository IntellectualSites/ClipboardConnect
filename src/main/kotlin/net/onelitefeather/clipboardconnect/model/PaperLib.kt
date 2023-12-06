package net.onelitefeather.clipboardconnect.model

/**
 * Represents a library configuration for a Paper plugin.
 *
 * @property repositories A map of repository names to their corresponding URLs, used for resolving dependencies.
 * @property dependencies A list of Maven-style dependency strings that specify the required libraries for the plugin.
 */
data class PaperLib(val repositories: Map<String, String>, val dependencies: List<String>) {
}