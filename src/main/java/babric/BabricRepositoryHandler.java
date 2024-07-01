/**
Swiped from https://github.com/Legacy-Fabric/legacy-looming/blob/dev/1.7/src/main/java/net/legacyfabric/legacylooming/LegacyRepositoryHandler.java with permission.
**/

package babric;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.plugins.PluginAware;
import org.jetbrains.annotations.NotNull;

public class BabricRepositoryHandler implements Plugin<PluginAware> {
    @Override
    public void apply(@NotNull PluginAware pluginAware) {
        if (pluginAware instanceof Settings settings) {
            declareRepositories(settings.getDependencyResolutionManagement().getRepositories());

            settings.getGradle().getPluginManager().apply(BabricRepositoryHandler.class);
        } else if (pluginAware instanceof Project project) {
            if (project.getGradle().getPlugins().hasPlugin(BabricRepositoryHandler.class)) {
                return;
            }

            declareRepositories(project.getRepositories());
        } else if (pluginAware instanceof Gradle) {
            return;
        } else {
            throw new IllegalArgumentException("Expected target to be a Project or Settings, but was a " + pluginAware.getClass());
        }
    }

    private void declareRepositories(RepositoryHandler repositories) {
        repositories.maven(repo -> {
            repo.setName("Legacy Fabric");
            repo.setUrl(Constants.LEGACYFABRIC_MAVEN);
            repo.content(content -> {
                content.includeGroup("org.lwjgl.lwjgl"); // Only for LWJGL
            });
        });
        repositories.maven(repo -> { // No filter, all parts of this repo are used in a project.
            repo.setName("Babric");
            repo.setUrl(Constants.BABRIC_MAVEN);
        });
        repositories.maven(repo -> { // No filter, all parts of this repo are used in a project.
            repo.setName("Modrinth");
            repo.setUrl(Constants.MODRINTH_MAVEN);
            repo.content(content -> {
                content.includeModule("maven.modrinth", "gambac"); // Allow only gambac, don't let folk rely on this adding MR for them.
            });
        });
    }
}
