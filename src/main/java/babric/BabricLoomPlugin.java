package babric;

import babric.mappings.BabricIntermediaryProvider;
import babric.processor.GambacLibraryProcessor;
import babric.processor.LWJGL2LibraryProcessor;
import babric.processor.NestFixingJarProcessor;
import net.fabricmc.loom.LoomGradleExtension;
import net.fabricmc.loom.api.LoomGradleExtensionAPI;
import net.fabricmc.loom.configuration.providers.minecraft.library.LibraryProcessorManager;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginAware;
import org.gradle.api.provider.ListProperty;

import java.util.Map;
import java.util.Objects;

public class BabricLoomPlugin implements Plugin<PluginAware> {
    public static final String VERSION = Objects.requireNonNullElse(BabricLoomPlugin.class.getPackage().getImplementationVersion(), "0.0.0+unknown");

    @Override
    public void apply(PluginAware target) {
        target.apply(Map.of("plugin", "fabric-loom"));
        target.getPlugins().apply(BabricRepositoryHandler.class);

        if (target instanceof Project project) applyProject(project);
    }

    private void applyProject(Project project) {
        project.getLogger().lifecycle("Babric loom: " + VERSION);

        ListProperty<LibraryProcessorManager.LibraryProcessorFactory> libraryProcessors = LoomGradleExtension.get(project).getLibraryProcessors();
        libraryProcessors.add(LWJGL2LibraryProcessor::new);

        LoomGradleExtensionAPI extension = (LoomGradleExtensionAPI) project.getExtensions().getByName("loom");

        BabricExtension babricExtension = project.getExtensions().create("babric", BabricExtension.class, project);

        libraryProcessors.add((platform, libraryContext) -> new GambacLibraryProcessor(platform, libraryContext, extension, project, babricExtension));

        extension.getVersionsManifests().add("babric-manifest", "https://babric.github.io/manifest-polyfill/version_manifest_v2.json", -10);

        extension.setIntermediateMappingsProvider(BabricIntermediaryProvider.class, provider -> {
            provider.getIntermediaryUrl().set("https://maven.glass-launcher.net/babric/babric/intermediary/%1$s/intermediary-%1$s-v2.jar");
            provider.getRefreshDeps().set(project.provider(() -> LoomGradleExtension.get(project).refreshDeps()));
        });

        extension.addMinecraftJarProcessor(NestFixingJarProcessor.class);
    }
}
