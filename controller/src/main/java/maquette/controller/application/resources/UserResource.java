package maquette.controller.application.resources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import maquette.controller.application.util.ContextUtils;
import maquette.controller.domain.CoreApplication;

@AllArgsConstructor
@RestController("User")
@RequestMapping("api/v1/user")
public class UserResource {

    private final CoreApplication core;

    private final ContextUtils ctx;

}
