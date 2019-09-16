package maquette.controller.application.resources;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import org.apache.avro.util.ByteBufferOutputStream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import akka.japi.Pair;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import maquette.controller.adapters.cli.CommandResult;
import maquette.controller.adapters.cli.commands.Command;
import maquette.controller.application.util.ContextUtils;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.util.Operators;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.core.records.Records;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.exceptions.DomainException;
import maquette.controller.domain.values.iam.User;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController("CLI")
@RequestMapping("api/v1/cli")
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
            .thenCompose(user -> command.run(user, core))
            .exceptionally(throwable -> Operators
                .hasCause(throwable, DomainException.class)
                .map(e -> CommandResult.error(e.getMessage()))
                .orElseGet(() -> {
                    LOG.error(throwable.getMessage(), throwable);
                    return CommandResult.error("An exception occurred on Maquette controller. Sorry bro ¯\\_(ツ)_/¯");
                }));
    }

    @RequestMapping(
        method = RequestMethod.PUT,
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @ApiOperation(
        value = "Push data into a dataset")
    public CompletionStage<String> pushData(
        @RequestPart("id") String id,
        @RequestPart("file") Mono<FilePart> file,
        ServerWebExchange exchange) {

        return file
            .toFuture()
            .thenCompose(filePart -> filePart
                .content()
                .map(b -> {
                    System.out.println(b.capacity());
                    return b;
                })
                .collect(Collectors.counting())
                .toFuture())
            .thenApply(fp -> "Ok");
    }

}
