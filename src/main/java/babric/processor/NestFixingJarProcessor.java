package babric.processor;

import net.fabricmc.loom.api.processor.MinecraftJarProcessor;
import net.fabricmc.loom.api.processor.ProcessorContext;
import net.fabricmc.loom.api.processor.SpecContext;
import net.fabricmc.stitch.commands.CommandFixNesting;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;

public class NestFixingJarProcessor implements MinecraftJarProcessor<NestFixingJarProcessor.Spec> {

    @Override
    public @Nullable NestFixingJarProcessor.Spec buildSpec(SpecContext specContext) {
        return new Spec();
    }

    @Override
    public void processJar(Path path, Spec spec, ProcessorContext processorContext) throws IOException {
        CommandFixNesting.run(path.toFile());
    }

    @Override
    public String getName() {
        return "babric:fix-nesting";
    }

    public static class Spec implements MinecraftJarProcessor.Spec {}
}
