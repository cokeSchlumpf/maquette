package maquette.controller.application.resources;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.avro.util.ByteBufferInputStream;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import maquette.controller.application.model.DatasetAccessRequest;
import maquette.controller.application.model.PublishDatasetVersionRequest;
import maquette.controller.application.util.ContextUtils;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.core.ResourcePath;
import maquette.controller.domain.values.core.UID;
import maquette.controller.domain.values.dataset.DatasetDetails;
import maquette.controller.domain.values.dataset.VersionDetails;
import maquette.controller.domain.values.dataset.VersionTag;
import maquette.controller.domain.values.iam.Authorization;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController("Datasets")
@RequestMapping("api/v1/datasets")
public class DatasetsResource {

    private final CoreApplication core;

    private final ContextUtils ctx;

    @RequestMapping(
        path = "{namespace}/{name}/settings/owner",
        method = RequestMethod.POST,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Change the owner of a dataset")
    public CompletionStage<DatasetDetails> changeOwner(
        @PathVariable("namespace") String namespace,
        @PathVariable("name") String name,
        @RequestBody Authorization owner,
        ServerWebExchange exchange) {

        return ctx
            .getUser(exchange)
            .thenCompose(user -> {
                ResourcePath dataset = ResourcePath.apply(user, namespace, name);

                return core
                    .datasets()
                    .changeOwner(user, dataset, owner);
            });
    }

    @RequestMapping(
        path = "{namespace}/{name}",
        method = RequestMethod.PUT,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Create a new dataset")
    public CompletionStage<DatasetDetails> create(
        @PathVariable("namespace") String namespace,
        @PathVariable("name") String name,
        @RequestParam("private") boolean isPrivate,
        ServerWebExchange exchange) {

        return ctx
            .getUser(exchange)
            .thenCompose(user -> {
                ResourcePath dataset = ResourcePath.apply(user, namespace, name);

                return core
                    .datasets()
                    .createDataset(user, dataset, isPrivate);
            });
    }

    @RequestMapping(
        path = "{namespace}/{name}/versions",
        method = RequestMethod.POST,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Create a new dataset version")
    public CompletionStage<UID> createDatasetVersion(
        @PathVariable("namespace") String namespace,
        @PathVariable("name") String name,
        @RequestBody Schema schema,
        ServerWebExchange exchange) {

        return ctx
            .getUser(exchange)
            .thenCompose(user -> {
                ResourcePath dataset = ResourcePath.apply(user, namespace, name);

                return core
                    .datasets()
                    .createDatasetVersion(user, dataset, schema);
            });
    }

    @RequestMapping(
        path = "{namespace}/{name}",
        method = RequestMethod.DELETE,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Delete an existing dataset")
    public CompletionStage<Void> delete(
        @PathVariable("namespace") String namespace,
        @PathVariable("name") String name,
        ServerWebExchange exchange) {

        return ctx
            .getUser(exchange)
            .thenCompose(user -> {
                ResourcePath dataset = ResourcePath.apply(user, namespace, name);

                return core
                    .datasets()
                    .deleteDataset(user, dataset)
                    .thenRun(() -> {});
            });
    }

    @RequestMapping(
        path = "{namespace}/{name}/versions/latest/data",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Read data from a dataset; version might be 'latest'")
    public CompletionStage<Resource> getData(
        @PathVariable("namespace") String namespace,
        @PathVariable("name") String name,
        ServerWebExchange exchange) {

        return ctx
            .getUser(exchange)
            .thenCompose(user -> {
                ResourcePath resource = ResourcePath.apply(user, namespace, name);

                return core
                    .datasets()
                    .getData(user, resource);
            })
            .thenApply(records -> {
                List<ByteBuffer> buffers = records
                    .getBytes()
                    .stream()
                    .map(ByteString::asByteBuffer)
                    .collect(Collectors.toList());

                ByteBufferInputStream is = new ByteBufferInputStream(buffers);
                return new InputStreamResource(is);
            });
    }

    @RequestMapping(
        path = "{namespace}/{name}/versions/{version}/data",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @ApiOperation(
        value = "Read data from a dataset; version might be 'latest'")
    public CompletionStage<Resource> getData(
        @PathVariable("namespace") String namespace,
        @PathVariable("name") String name,
        @PathVariable("version") String version,
        ServerWebExchange exchange) {

        return ctx
            .getUser(exchange)
            .thenCompose(user -> {
                ResourcePath resource = ResourcePath.apply(user, namespace, name);
                VersionTag versionTag = VersionTag.apply(version);

                return core
                    .datasets()
                    .getData(user, resource, versionTag);
            })
            .thenApply(records -> {
                List<ByteBuffer> buffers = records
                    .getBytes()
                    .stream()
                    .map(ByteString::asByteBuffer)
                    .collect(Collectors.toList());

                ByteBufferInputStream is = new ByteBufferInputStream(buffers);
                return new InputStreamResource(is);
            });
    }

    @RequestMapping(
        path = "{namespace}/{name}",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Create a new dataset")
    public CompletionStage<DatasetDetails> getDetails(
        @PathVariable("namespace") String namespace,
        @PathVariable("name") String name,
        ServerWebExchange exchange) {

        return ctx
            .getUser(exchange)
            .thenCompose(user -> {
                ResourcePath dataset = ResourcePath.apply(user, namespace, name);

                return core
                    .datasets()
                    .getDetails(user, dataset);
            });
    }

    @RequestMapping(
        path = "{namespace}/{name}/versions/{version}",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Read data from a dataset; version might be 'latest'")
    public CompletionStage<VersionDetails> getVersionDetails(
        @PathVariable("namespace") String namespace,
        @PathVariable("name") String name,
        @PathVariable("version") String version,
        ServerWebExchange exchange) {

        return ctx
            .getUser(exchange)
            .thenCompose(user -> {
                ResourcePath dataset = ResourcePath.apply(user, namespace, name);

                if (version.equals("latest")) {
                    return core
                        .datasets()
                        .getVersionDetails(user, dataset);
                } else {
                    return core
                        .datasets()
                        .getVersionDetails(user, dataset, VersionTag.apply(version));
                }
            });
    }

    @RequestMapping(
        path = "{namespace}/{name}/settings/access",
        method = RequestMethod.PUT,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Change the owner of a dataset")
    public CompletionStage<DatasetDetails> grant(
        @PathVariable("namespace") String namespace,
        @PathVariable("name") String name,
        @RequestBody DatasetAccessRequest request,
        ServerWebExchange exchange) {

        return ctx
            .getUser(exchange)
            .thenCompose(user -> {
                ResourcePath dataset = ResourcePath.apply(user, namespace, name);

                return core
                    .datasets()
                    .grantDatasetAccess(user, dataset, request.getPrivilege(), request.getAuthorization());
            });
    }

    @RequestMapping(
        path = "{namespace}/{name}/versions/{id}",
        method = RequestMethod.PATCH,
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @ApiOperation(
        value = "Push data into a dataset")
    public CompletionStage<VersionDetails> pushData(
        @PathVariable("namespace") String namespace,
        @PathVariable("name") String name,
        @PathVariable("id") String id,
        @RequestPart("file") Mono<FilePart> file,
        ServerWebExchange exchange) {

        UID uid = UID.apply(id);

        return file
            .toFuture()
            .thenCompose(filePart -> {
                Source<ByteBuffer, NotUsed> data = Source
                    .fromPublisher(filePart.content())
                    .map(DataBuffer::asByteBuffer);

                return ctx
                    .getUser(exchange)
                    .thenCompose(user -> {
                        ResourcePath dataset = ResourcePath.apply(user, namespace, name);

                        return core
                            .datasets()
                            .pushData(user, dataset, uid, data);
                    });
            });
    }

    @RequestMapping(
        path = "{namespace}/{name}/versions",
        method = RequestMethod.PUT,
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @ApiOperation(
        value = "Push data into a dataset")
    public CompletionStage<VersionTag> putData(
        @PathVariable("namespace") String namespace,
        @PathVariable("name") String name,
        @RequestPart("message") String message,
        @RequestPart("file") Mono<FilePart> file,
        ServerWebExchange exchange) {

        return file
            .toFuture()
            .thenCompose(filePart -> {
                Source<ByteBuffer, NotUsed> data = Source
                    .fromPublisher(filePart.content())
                    .map(DataBuffer::asByteBuffer);

                return ctx
                    .getUser(exchange)
                    .thenCompose(user -> {
                        ResourcePath dataset = ResourcePath.apply(user, namespace, name);

                        return core
                            .datasets()
                            .putData(user, dataset, data, message);
                    });
            });
    }

    @RequestMapping(
        path = "{namespace}/{name}/versions/{id}",
        method = RequestMethod.POST,
        produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    @ApiOperation(
        value = "Publish a new dataset version")
    public CompletionStage<VersionTag> publishDatasetVersion(
        @PathVariable("namespace") String namespace,
        @PathVariable("name") String name,
        @PathVariable("id") String id,
        @RequestBody PublishDatasetVersionRequest request,
        ServerWebExchange exchange) {

        return ctx
            .getUser(exchange)
            .thenCompose(user -> {
                ResourcePath dataset = ResourcePath.apply(user, namespace, name);
                UID uid = UID.apply(id);

                return core
                    .datasets()
                    .publishDatasetVersion(user, dataset, uid, request.getMessage());
            });
    }

    @RequestMapping(
        path = "{namespace}/{name}/settings/access",
        method = RequestMethod.DELETE,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
        value = "Change the owner of a dataset")
    public CompletionStage<DatasetDetails> revoke(
        @PathVariable("namespace") String namespace,
        @PathVariable("name") String name,
        @RequestBody DatasetAccessRequest request,
        ServerWebExchange exchange) {

        return ctx
            .getUser(exchange)
            .thenCompose(user -> {
                ResourcePath dataset = ResourcePath.apply(user, namespace, name);

                return core
                    .datasets()
                    .revokeDatasetAccess(user, dataset, request.getPrivilege(), request.getAuthorization());
            });
    }

}
