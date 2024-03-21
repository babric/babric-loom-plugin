package babric.mappings;

import net.fabricmc.loom.configuration.providers.mappings.IntermediaryMappingsProvider;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.MappingWriter;
import net.fabricmc.mappingio.adapter.MappingDstNsReorder;
import net.fabricmc.mappingio.adapter.MappingNsRenamer;
import net.fabricmc.mappingio.format.MappingFormat;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public abstract class BabricIntermediaryProvider extends IntermediaryMappingsProvider {
    @Override
    public @NotNull String getName() {
        return "babric-" + super.getName();
    }

    @Override
    public void provide(Path tinyMappings, @Nullable Project project) throws IOException {
        if (!Files.exists(tinyMappings) || this.getRefreshDeps().get()) {
            Path intermediaryTempPath = tinyMappings.getParent().resolve(this.getName() + "-temp.tiny");
            super.provide(intermediaryTempPath, project);

            MemoryMappingTree mappingTree = new MemoryMappingTree();

            MappingReader.read(intermediaryTempPath, mappingTree);

            MappingWriter writer = MappingWriter.create(tinyMappings, MappingFormat.TINY_2_FILE);

            MappingDstNsReorder reorder = new MappingDstNsReorder(writer, List.of("clientOfficial", "serverOfficial"));
            MappingNsRenamer renamer = new MappingNsRenamer(reorder, Map.of("client", "clientOfficial", "server", "serverOfficial"));

            if (mappingTree.getDstNamespaces().contains("glue")) mappingTree.accept(renamer);
            else mappingTree.accept(writer);

            Files.delete(intermediaryTempPath);
        }
    }
}
