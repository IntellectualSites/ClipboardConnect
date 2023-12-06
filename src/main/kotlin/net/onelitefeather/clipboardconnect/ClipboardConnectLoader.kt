package net.onelitefeather.clipboardconnect

import com.fasterxml.jackson.databind.ObjectMapper
import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository


class ClipboardConnectLoader : PluginLoader {
    private val mapper = ObjectMapper()
    override fun classloader(classpathBuilder: PluginClasspathBuilder) {
        val libs = javaClass.classLoader.getResourceAsStream("paper-libraries.json")
        val jsonNode = mapper.readTree(libs)
        val repositories = jsonNode["repositories"]
        val resolver = MavenLibraryResolver()
        repositories.fields().forEachRemaining {
            val name = it.key
            val url = it.value.asText()
            resolver.addRepository(RemoteRepository.Builder(name, "default", url).build())
        }
        val dependencies = jsonNode["dependencies"]
        dependencies.asIterable().forEach {
            val dependency = it.asText()
            resolver.addDependency(Dependency(DefaultArtifact(dependency), null))
        }
    }
}