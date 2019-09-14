package maquette.controller.application.resources;

import java.util.concurrent.CompletionStage;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.application.util.ContextUtils;
import maquette.controller.domain.CoreApplication;

@AllArgsConstructor
@RestController("CLI")
@RequestMapping("api/v1/cli")
public class CommandsResource {

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
            .thenCompose(user -> command.run(user, core));
    }

}
