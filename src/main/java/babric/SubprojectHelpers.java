package babric;

import groovy.util.Node;
import net.fabricmc.loom.util.GroovyXmlUtil;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A utility class which can be used in multi-module builds to neatly tie multi-mod structures together.
 */
public class SubprojectHelpers {

    /**
     * Adds the provided subprojects to the parent project as dependencies. A must-have for multi-module projects.
     * @param project The root project to add the subprojects to.
     * @param projectNames The names of the subprojects to be added.
     */
    public static void addModuleDependencies(Project project, String... projectNames) {
        List<Dependency> modules = Arrays.stream(projectNames).map((it) -> project.getDependencies().project(Map.of("path", ":" + it, "configuration", "dev"))).collect(Collectors.toList());

        modules.forEach(dependency -> project.getDependencies().add("implementation", dependency));

        MavenPublication publishing = (MavenPublication) project.getExtensions().getByType(PublishingExtension.class).getPublications().getByName("mavenJava");
        publishing.pom((e) -> e.withXml((f) -> {
            addDependencyXMLs(f.asNode(), "implementation", modules);
        }));
    }

    /**
     * Adds a project to the provided maven pom. Used for fixing maven poms elsewhere, and is safe to be used by multi-module projects.
     * @param xml The maven pom XML root node.
     * @param scope The scope of the dependency. Usually "implementation".
     * @param dependency The project to add as a dependency.
     */
    public static void addDependencyXML(Node xml, String scope, Project dependency) {
        Node depsNode = GroovyXmlUtil.getOrCreateNode(xml, "dependencies");

        Node appNode = depsNode.appendNode("dependency");
        appNode.appendNode("groupId", dependency.getGroup());
        appNode.appendNode("artifactId", dependency.getName());
        appNode.appendNode("version", dependency.getVersion());
        appNode.appendNode("scope", scope);
    }

    /**
     * Adds a list of projects to the maven pom. Used for fixing maven poms elsewhere, and is safe to be used by multi-module projects.
     * @param xml The maven pom XML root node.
     * @param scope The scope of the dependency. Usually "implementation".
     * @param dependencies The projects to add as a dependency.
     */
    public static void addDependencyXMLs(Node xml, String scope, List<Dependency> dependencies) {
        Node depsNode = GroovyXmlUtil.getOrCreateNode(xml, "dependencies");

        for (Dependency dep : dependencies) {
            Node appNode = depsNode.appendNode("dependency");
            appNode.appendNode("groupId", dep.getGroup());
            appNode.appendNode("artifactId", dep.getName());
            appNode.appendNode("version", dep.getVersion());
            appNode.appendNode("scope", scope);
        }
    }
}
