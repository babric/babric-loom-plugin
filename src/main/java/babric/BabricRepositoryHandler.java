package babric;

import org.gradle.api.artifacts.dsl.RepositoryHandler;

public class BabricRepositoryHandler {
    protected static void declareRepositories(RepositoryHandler repositories) {
        repositories.maven(repo -> {
            repo.setName("Babric");
            repo.setUrl(Constants.MAVEN);
            repo.content(content -> {
                content.includeGroup("babric");
                content.includeGroupByRegex("babric.*");
                content.includeGroup("org.lwjgl.lwjgl");
            });
        });
    }
}
