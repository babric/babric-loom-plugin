/**
Swiped from https://github.com/Legacy-Fabric/legacy-looming/blob/dev/1.7/src/main/java/net/legacyfabric/legacylooming/providers/LWJGL2LibraryProcessor.java with permission.
**/

package babric.processor;

import babric.BabricExtension;
import net.fabricmc.loom.api.LoomGradleExtensionAPI;
import net.fabricmc.loom.configuration.providers.minecraft.library.Library;
import net.fabricmc.loom.configuration.providers.minecraft.library.LibraryContext;
import net.fabricmc.loom.configuration.providers.minecraft.library.LibraryProcessor;
import net.fabricmc.loom.util.Platform;
import org.gradle.api.Project;
import org.gradle.api.artifacts.dsl.RepositoryHandler;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class GambacLibraryProcessor extends LibraryProcessor {
    public static final String VERSION = "1.1.1";
    private boolean applied = false;
    private final LoomGradleExtensionAPI loom;
    private final Project project;
    private final BabricExtension babricExtension;

    public GambacLibraryProcessor(Platform platform, LibraryContext context, LoomGradleExtensionAPI loom, Project project, BabricExtension babricExtension) {
        super(platform, context);
        this.loom = loom;
        this.project = project;
        this.babricExtension = babricExtension;
    }

    @Override
    public ApplicationResult getApplicationResult() {
        if (babricExtension.disableM1Fixes.get()) {
            return ApplicationResult.DONT_APPLY;
        }
        if (!loom.getMinecraftVersion().get().equals("b1.7.3")) {

            project.getLogger().warn("M1 fix being ignored. Unsupported minecraft version.");
            return ApplicationResult.DONT_APPLY;
        }

        return ApplicationResult.MUST_APPLY;
    }

    @Override
    public Predicate<Library> apply(Consumer<Library> dependencyConsumer) {
        if (!applied) {
            applied = true;

            dependencyConsumer.accept(Library.fromMaven("net.danygames2014:gambac:" + VERSION, Library.Target.LOCAL_MOD));
        }

        return ALLOW_ALL;
    }
}
