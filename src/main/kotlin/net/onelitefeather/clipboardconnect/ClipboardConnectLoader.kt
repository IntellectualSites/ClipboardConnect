package net.onelitefeather.clipboardconnect

import com.google.gson.Gson
import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import net.onelitefeather.clipboardconnect.model.PaperLib
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository
import java.io.InputStreamReader


class ClipboardConnectLoader : PluginLoader {
    override fun classloader(classpathBuilder: PluginClasspathBuilder) {
        val gson = Gson()
        val libs = javaClass.classLoader.getResourceAsStream("paper-libraries.json")
        val lib = gson.fromJson(InputStreamReader(libs), PaperLib::class.java)
        val resolver = MavenLibraryResolver()
        lib.repositories.forEach { name, url ->
            resolver.addRepository(RemoteRepository.Builder(name, "default", url).build())
        }
        lib.dependencies.forEach {
            resolver.addDependency(Dependency(DefaultArtifact(it), null))
        }
        classpathBuilder.addLibrary(resolver)
    }
}