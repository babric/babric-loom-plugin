package babric;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;

public class BabricExtension {
    public final Property<Boolean> disableM1Fixes;

    public final Property<Boolean> disablePomOverride;

    public BabricExtension(Project project) {
        disableM1Fixes = project.getObjects().property(Boolean.class).convention(false);
        disablePomOverride = project.getObjects().property(Boolean.class).convention(false);
    }
}
