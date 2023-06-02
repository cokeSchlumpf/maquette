package maquette.common.commands;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import maquette.common.Operators;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DomainCommands {

    public final Map<String, Class<? extends Command<?>>> commands;

    public static DomainCommands applyMap(Map<String, Class<? extends Command<?>>> commands) {
        return new DomainCommands(commands);
    }

    public static DomainCommands apply(Set<Class<? extends Command<?>>> commands) {
        var commandsAsMap = commands
            .stream()
            .collect(Collectors.<Class<? extends Command<?>>, String, Class<? extends Command<?>>>toMap(
                cmd -> Operators.camelCaseToKebabCase(cmd.getSimpleName()).replaceAll("-command$", ""),
                cmd -> cmd
            ));

        return DomainCommands.applyMap(commandsAsMap);
    }

    public Class<? extends Command<?>> getCommand(String name) {
        var cmd = this.commands.get(name);

        if (Objects.isNull(cmd)) {
            throw new RuntimeException("Command Not Found!"); // TODO: Better exception.
        }

        return cmd;
    }

}
