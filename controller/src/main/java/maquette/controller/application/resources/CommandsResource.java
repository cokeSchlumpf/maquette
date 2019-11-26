package maquette.controller.application.resources;

import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import maquette.controller.application.util.ContextUtils;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.api.commands.CommandResult;
import maquette.controller.domain.api.commands.OutputFormat;
import maquette.controller.domain.api.commands.ViewModel;
import maquette.controller.domain.api.commands.commands.Command;
import maquette.controller.domain.api.commands.commands.datasets.PrintDatasetDetailsCmd;
import maquette.controller.domain.api.commands.views.ErrorVM;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.core.ResourceName;
import maquette.controller.domain.values.exceptions.DomainException;

@AllArgsConstructor
@RestController("CLI")
@RequestMapping("api/v1/commands")
public class CommandsResource {

    private static final Logger LOG = LoggerFactory.getLogger(CommandsResource.class);

    private final CoreApplication core;

    private final ContextUtils ctx;

    private final ObjectMapper om;

    private CompletionStage<ViewModel> processCommand(Command command, ServerWebExchange exchange) {
        return ctx
            .getUser(exchange)
            .thenCompose(user -> command.run(user, core, OutputFormat.apply()))
            .exceptionally(throwable -> Operators
                .hasCause(throwable, DomainException.class)
                .map(e -> ErrorVM.apply(e.getMessage()))
                .orElseGet(() -> {
                    LOG.error(throwable.getMessage(), throwable);
                    return ErrorVM.apply("An exception occurred on Maquette controller. Sorry bro ¯\\_(ツ)_/¯");
                }));
    }

    @RequestMapping(
        method = RequestMethod.POST,
        produces = {MediaType.TEXT_PLAIN_VALUE})
    public CompletionStage<String> processText(@RequestBody Command command, ServerWebExchange exchange) {
        return processCommand(command, exchange).thenApply(vm -> vm.toCommandResult(om).getOutput());
    }

    @RequestMapping(
        method = RequestMethod.POST,
        produces = {"text/cmd"})
    public CompletionStage<CommandResult> processCmd(@RequestBody Command command, ServerWebExchange exchange) {
        return processCommand(command, exchange).thenApply(vm -> vm.toCommandResult(om));
    }

    @RequestMapping(
        method = RequestMethod.POST,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public CompletionStage<ViewModel> processStructured(@RequestBody Command command, ServerWebExchange exchange) {
        return processCommand(command, exchange);
    }

    @RequestMapping(
        path = "example",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public Command example() {
        return PrintDatasetDetailsCmd.apply(ResourceName.apply("foo"), ResourceName.apply("bla"));
    }

}
