package babric.processor;

import net.fabricmc.loom.api.processor.MinecraftJarProcessor;
import net.fabricmc.loom.api.processor.ProcessorContext;
import net.fabricmc.loom.api.processor.SpecContext;
import net.fabricmc.stitch.commands.CommandFixNesting;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class NestFixingJarProcessor implements MinecraftJarProcessor<NestFixingJarProcessor.Spec> {

    @Override
    public @Nullable NestFixingJarProcessor.Spec buildSpec(SpecContext specContext) {
        return new Spec();
    }

    @Override
    public void processJar(Path path, Spec spec, ProcessorContext processorContext) throws IOException {
        File file = path.getParent().resolve(".fixedNest").toFile();

        if (!file.exists()) {
            CommandFixNesting.run(path.toFile());
            file.createNewFile();
        }
    }

    @Override
    public String getName() {
        return "babric:fix-nesting";
    }

    public static class Spec implements MinecraftJarProcessor.Spec {}
}
