package babric;

import babric.mappings.BabricIntermediaryProvider;
import babric.processor.GambacLibraryProcessor;
import babric.processor.LWJGL2LibraryProcessor;
import babric.processor.NestFixingJarProcessor;
import groovy.util.Node;
import groovy.util.NodeList;
import net.fabricmc.loom.LoomGradleExtension;
import net.fabricmc.loom.api.LoomGradleExtensionAPI;
import net.fabricmc.loom.configuration.providers.minecraft.library.LibraryProcessorManager;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.PluginAware;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.internal.os.OperatingSystem;

import java.util.Map;
import java.util.Objects;

public class BabricLoomPlugin implements Plugin<PluginAware> {
    public static final String VERSION = Objects.requireNonNullElse(BabricLoomPlugin.class.getPackage().getImplementationVersion(), "0.0.0+unknown");

    @Override
    public void apply(PluginAware target) {
        System.setProperty("fabric.loom.disableMinecraftVerification", "true");

        target.apply(Map.of("plugin", "fabric-loom"));
        target.getPlugins().apply(BabricRepositoryHandler.class);

        if (target instanceof Project project) applyProject(project);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void applyProject(Project project) {
        project.getLogger().lifecycle("Babric loom: " + VERSION);

        BabricExtension babricExtension = project.getExtensions().create("babric", BabricExtension.class, project);

        // Add a transitiveImplementation configuration because gradle has no built-in way to say "don't add this to the pom"
        Configuration transitiveImplementation = project.getConfigurations().create("transitiveImplementation");

        ListProperty<LibraryProcessorManager.LibraryProcessorFactory> libraryProcessors = LoomGradleExtension.get(project).getLibraryProcessors();
        libraryProcessors.add(LWJGL2LibraryProcessor::new);

        LoomGradleExtensionAPI extension = (LoomGradleExtensionAPI) project.getExtensions().getByName("loom");

        libraryProcessors.add((platform, libraryContext) -> new GambacLibraryProcessor(platform, libraryContext, extension, project, babricExtension));

        extension.getVersionsManifests().add("babric-manifest", "https://babric.github.io/manifest-polyfill/version_manifest_v2.json", -10);

        extension.setIntermediateMappingsProvider(BabricIntermediaryProvider.class, provider -> {
            provider.getIntermediaryUrl().set("https://maven.glass-launcher.net/babric/babric/intermediary-upstream/%1$s/intermediary-upstream-%1$s-v2.jar");
            provider.getRefreshDeps().set(project.provider(() -> LoomGradleExtension.get(project).refreshDeps()));
        });

        extension.addMinecraftJarProcessor(NestFixingJarProcessor.class);

        project.afterEvaluate(p -> {
            if (OperatingSystem.current().isMacOsX()) {
                extension.getRunConfigs().configureEach(runConfig -> {
                    if (runConfig.getName().equals("client")) {
                        runConfig.getVmArgs().add("-Dapple.awt.application.appearance=system");
                    }
                });
            }
        });

        // Wipes the normal pom's dependency block and substitutes in the transitiveImplementation configuration contents.
        PublishingExtension publishing = project.getExtensions().getByType(PublishingExtension.class);
        project.afterEvaluate(p -> {
            if (!babricExtension.disablePomOverride.get()) {
                publishing.getPublications().forEach(publication -> {
                    if (publication instanceof MavenPublication mavenPublication) {
                        mavenPublication.pom(mavenPom -> mavenPom.withXml(xmlProvider -> {
                            Node depsNode = new Node(null, "dependencies");

                            // Jank solution to an annoying issue
                            transitiveImplementation.getDependencies().forEach(dependency -> {
                                Node depNode = depsNode.appendNode("dependency");
                                depNode.appendNode("groupId", dependency.getGroup());
                                depNode.appendNode("artifactId", dependency.getName());
                                depNode.appendNode("version", dependency.getVersion());
                                depNode.appendNode("scope", "runtime");
                            });

                            // Replace the dependency block, because it's just hopelessly wrong and includes floader+asm for some reason
                            ((Node) ((NodeList) xmlProvider.asNode().get("dependencies")).get(0)).replaceNode(depsNode);
                        }));
                    }
                });
            }
        });
    }
}
