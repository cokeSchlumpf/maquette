package maquette.controller.domain.api.commands.views;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.ViewModel;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationVM implements ViewModel {

    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final String VERSION = "version";
    private static final String INSTANCE = "instance";

    @JsonProperty(BRAND)
    private final String brand;

    @JsonProperty(NAME)
    private final String name;

    @JsonProperty(VERSION)
    private final String version;

    @JsonProperty(INSTANCE)
    private final String instance;

    public static ApplicationVM apply(
        @JsonProperty(BRAND) String brand,
        @JsonProperty(NAME) String name,
        @JsonProperty(VERSION) String version,
        @JsonProperty(INSTANCE) String instance) {

        return new ApplicationVM(brand, name, version, instance);
    }

    @Override
    public CommandResult toCommandResult(ObjectMapper om) {
        String about = String.format("%s %s, %s, %s", brand, name, version, instance);
        return CommandResult.success(about);
    }
}
