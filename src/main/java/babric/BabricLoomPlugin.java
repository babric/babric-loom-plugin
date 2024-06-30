package babric;

import babric.mappings.BabricIntermediaryProvider;
import babric.processor.LWJGL2LibraryProcessor;
import babric.processor.NestFixingJarProcessor;
import net.fabricmc.loom.LoomGradleExtension;
import net.fabricmc.loom.api.LoomGradleExtensionAPI;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginAware;

import java.util.Map;
import java.util.Objects;

public class BabricLoomPlugin implements Plugin<PluginAware> {
    public static final String VERSION = Objects.requireNonNullElse(BabricLoomPlugin.class.getPackage().getImplementationVersion(), "0.0.0+unknown");

    @Override
    public void apply(PluginAware target) {
        target.apply(Map.of("plugin", "fabric-loom"));
        target.getPlugins().apply(LegacyRepositoryHandler.class);

        if (target instanceof Project project) applyProject(project);
    }

    private void applyProject(Project project) {
        project.getLogger().lifecycle("Babric loom: " + VERSION);

//        BabricRepositoryHandler.declareRepositories(project.getRepositories());

        LoomGradleExtension.get(project).getLibraryProcessors().add(LWJGL2LibraryProcessor::new);

        LoomGradleExtensionAPI extension = (LoomGradleExtensionAPI) project.getExtensions().getByName("loom");

        extension.setIntermediateMappingsProvider(BabricIntermediaryProvider.class, provider -> {
            provider.getIntermediaryUrl().set(extension.getIntermediaryUrl());
            provider.getRefreshDeps().set(project.provider(() -> LoomGradleExtension.get(project).refreshDeps()));
        });

        extension.addMinecraftJarProcessor(NestFixingJarProcessor.class);
    }
}
