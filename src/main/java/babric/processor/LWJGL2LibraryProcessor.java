/**
Swiped from https://github.com/Legacy-Fabric/legacy-looming/blob/dev/1.7/src/main/java/net/legacyfabric/legacylooming/providers/LWJGL2LibraryProcessor.java with permission.
**/

package babric.processor;

import net.fabricmc.loom.configuration.providers.minecraft.library.Library;
import net.fabricmc.loom.configuration.providers.minecraft.library.LibraryContext;
import net.fabricmc.loom.configuration.providers.minecraft.library.LibraryProcessor;
import net.fabricmc.loom.util.Platform;
import org.gradle.api.artifacts.dsl.RepositoryHandler;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class LWJGL2LibraryProcessor extends LibraryProcessor {
    public static final String VERSION = "2.9.4+legacyfabric.9";
    private boolean applied = false;
    public LWJGL2LibraryProcessor(Platform platform, LibraryContext context) {
        super(platform, context);
    }

    @Override
    public ApplicationResult getApplicationResult() {
        if (!context.usesLWJGL3()) {

            return ApplicationResult.MUST_APPLY;
        }

        return ApplicationResult.DONT_APPLY;
    }

    @Override
    public Predicate<Library> apply(Consumer<Library> dependencyConsumer) {
        return library -> {
            if (!applied) {
                applied = true;
                final Library[] libs = new Library[]{
                        Library.fromMaven("org.lwjgl.lwjgl:lwjgl-platform:" + VERSION + ":" + getNativeClassifier(), Library.Target.NATIVES),
                        Library.fromMaven("org.lwjgl.lwjgl:lwjgl_util:" + VERSION, Library.Target.COMPILE),
                        Library.fromMaven("org.lwjgl.lwjgl:lwjgl:" + VERSION, Library.Target.COMPILE)
                };

                for (Library lib : libs) {
                    dependencyConsumer.accept(lib);
                }
            }

            return !library.group().equals("org.lwjgl.lwjgl");
        };
    }

    public static String getNativeClassifier() {
        switch (Platform.CURRENT.getOperatingSystem()) {
            case MAC_OS -> {
                return "natives-osx";
            }
            case LINUX -> {
                return "natives-linux";
            }
            default -> {
                return "natives-windows";
            }
        }
    }
}
