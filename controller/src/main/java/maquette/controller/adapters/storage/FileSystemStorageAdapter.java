package maquette.controller.adapters.storage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.generic.GenericData;
import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import maquette.controller.domain.ports.DataStorageAdapter;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.records.Records;

@AllArgsConstructor(staticName = "apply")
public final class FileSystemStorageAdapter implements DataStorageAdapter {

    private final Path baseDirectory;

    public static FileSystemStorageAdapter apply() {
        return apply(new File("journal").toPath().resolve("data"));
    }

    @Override
    public void append(UID versionId, Records data) {
        Operators.suppressExceptions(() -> {
            Path versionDirectory = baseDirectory.resolve(versionId.getValue());
            Files.createDirectories(versionDirectory);

            ArrayList<Path> existing = Lists.newArrayList(Files.newDirectoryStream(versionDirectory).iterator());
            data.toFile(versionDirectory.resolve("records-" + existing.size() + ".avro"));
        });
    }

    @Override
    public void clean(UID versionId) {
        Operators.suppressExceptions(() -> {
            Path versionDirectory = baseDirectory.resolve(versionId.getValue());
            FileUtils.deleteDirectory(versionDirectory.toFile());
        });
    }

    @Override
    public Records get(UID versionId) {
        return Operators.suppressExceptions(() -> {
            Path versionDirectory = baseDirectory.resolve(versionId.getValue());

            if (!Files.exists(versionDirectory)) {
                return Records.empty();
            } else {
                ArrayList<Path> existing = Lists.newArrayList(Files.newDirectoryStream(versionDirectory).iterator());
                List<GenericData.Record> records = Lists.newArrayList();

                for (Path path : existing) {
                    Records r = Records.fromFile(path);
                    records.addAll(r.getRecords());
                }

                return Records.fromRecords(records);
            }
        });
    }

}
