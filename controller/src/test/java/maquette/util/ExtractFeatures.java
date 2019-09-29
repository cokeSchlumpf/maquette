package maquette.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import maquette.controller.domain.util.Operators;

@AllArgsConstructor(staticName = "apply")
public class ExtractFeatures {

    private final Path basePath;

    public static void main(String... args) {
        ExtractFeatures
            .apply(new File(".").toPath())
            .clean()
            .extract();
    }

    private ExtractFeatures clean() {
        return Operators.suppressExceptions(() -> {
            Files
                .walk(basePath.resolve("src/test/resources/features"))
                .filter(path -> path.toString().endsWith(".feature"))
                .forEach(path -> Operators.suppressExceptions(() -> Files.delete(path)));

            return this;
        });
    }

    private ExtractFeatures extract() {
        return Operators.suppressExceptions(() -> {
            Files
                .walk(basePath.resolve("src/test/resources/features"))
                .filter(path -> path.toString().endsWith(".md"))
                .forEach(this::extractFeature);

            return this;
        });
    }

    private void extractFeature(Path markdown) {
        System.out.println("Extract features for " + markdown);
        Operators.suppressExceptions(() -> {
            List<String> mdLines = Files.readAllLines(markdown);
            List<String> ftLines = Lists.newArrayList();
            boolean gherkin = false;

            for (String l : mdLines) {
                if (gherkin) {
                    if (l.startsWith("```")) {
                        gherkin = false;
                        ftLines.add("");
                        ftLines.add("");
                    } else {
                        ftLines.add(l);
                    }
                } else {
                    if (l.startsWith("```gherkin")) {
                        gherkin = true;
                    }
                }
            }

            if (ftLines.size() > 0) {
                Path featurePath = markdown.resolveSibling(markdown.getFileName().toString() + ".feature");
                Files.write(featurePath, ftLines, StandardCharsets.UTF_8);
            }
        });
    }

}
