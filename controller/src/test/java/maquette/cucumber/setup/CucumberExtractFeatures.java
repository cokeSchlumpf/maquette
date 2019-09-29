package maquette.cucumber.setup;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import maquette.controller.domain.util.Operators;

@AllArgsConstructor(staticName = "apply")
public class CucumberExtractFeatures {

    private final Path basePath;

    public static void main(String... args) {
        CucumberExtractFeatures
            .apply(new File(".").toPath())
            .clean()
            .extract();
    }

    private CucumberExtractFeatures clean() {
        return Operators.suppressExceptions(() -> {
            Files
                .walk(basePath.resolve("src/test/resources/features"))
                .filter(path -> path.toString().endsWith(".feature"))
                .forEach(path -> Operators.suppressExceptions(() -> Files.delete(path)));

            return this;
        });
    }

    private CucumberExtractFeatures extract() {
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

                System.out.println("Saving " + featurePath + " ...");

                Files.write(featurePath, ftLines, StandardCharsets.UTF_8);
            }
        });
    }

}
