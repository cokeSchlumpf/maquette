package maquette.controller.application.commands;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommandResult {

    private final String error;

    private final String output;

    private final List<DataTable> data;

    private final ViewModel view;

    public static CommandResult error(String error) {
        return new CommandResult(error, null, ImmutableList.of(), null);
    }

    public static CommandResult success(String output, List<DataTable> data) {
        return new CommandResult(null, output, ImmutableList.copyOf(data), null);
    }

    public static CommandResult success(String output, DataTable ...data) {
        return new CommandResult(null, output, ImmutableList.copyOf(data), null);
    }

    public static CommandResult success(String output) {
        return success(output, Lists.newArrayList());
    }

    public static CommandResult success(List<DataTable> data) {
        return success(null, data);
    }

    public static CommandResult success() {
        return success(null, Lists.newArrayList());
    }

    public static CommandResult success(DataTable ...data) {
        return success(null, Lists.newArrayList(data));
    }

    public CommandResult withView(ViewModel view) {
        return new CommandResult(error, output, data, view);
    }

}
