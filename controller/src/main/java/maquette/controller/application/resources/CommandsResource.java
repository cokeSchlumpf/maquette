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

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import maquette.controller.application.commands.CommandResult;
import maquette.controller.application.commands.OutputFormat;
import maquette.controller.application.commands.commands.Command;
import maquette.controller.application.util.ContextUtils;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.exceptions.DomainException;

@AllArgsConstructor
@RestController("CLI")
@RequestMapping("api/v1/commands")
public class CommandsResource {

    private static final Logger LOG = LoggerFactory.getLogger(CommandsResource.class);

    private final CoreApplication core;

    private final ContextUtils ctx;

    @RequestMapping(
        method = RequestMethod.POST,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Change the owner of a dataset")
    public CompletionStage<CommandResult> process(@RequestBody Command command, ServerWebExchange exchange) {
        return ctx
            .getUser(exchange)
            .thenCompose(user -> command.run(user, core, OutputFormat.apply()))
            .exceptionally(throwable -> Operators
                .hasCause(throwable, DomainException.class)
                .map(e -> CommandResult.error(e.getMessage()))
                .orElseGet(() -> {
                    LOG.error(throwable.getMessage(), throwable);
                    return CommandResult.error("An exception occurred on Maquette controller. Sorry bro ¯\\_(ツ)_/¯");
                }));
    }

}
