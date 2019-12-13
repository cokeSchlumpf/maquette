package maquette.controller.application.resources;

import java.util.concurrent.CompletionStage;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import lombok.AllArgsConstructor;
import maquette.controller.application.util.ContextUtils;
import maquette.controller.domain.values.iam.User;

@AllArgsConstructor
@RestController("About")
@RequestMapping("api/v1/about")
public class AboutResource {

    private final ContextUtils ctx;

    @RequestMapping(
        path = "/user",
        method = RequestMethod.GET,
        produces = {MediaType.APPLICATION_JSON_VALUE})
    public CompletionStage<User> getUser(ServerWebExchange exchange) {
        return ctx.getUser(exchange);
    }

}
