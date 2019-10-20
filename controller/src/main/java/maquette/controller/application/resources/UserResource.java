package maquette.controller.application.resources;

import java.util.Set;
import java.util.concurrent.CompletionStage;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import lombok.AllArgsConstructor;
import maquette.controller.application.util.ContextUtils;
import maquette.controller.domain.CoreApplication;
import maquette.controller.domain.values.dataset.DatasetDetails;

@AllArgsConstructor
@RestController("User")
@RequestMapping("api/v1/user")
public class UserResource {

    private final CoreApplication core;

    private final ContextUtils ctx;

}
