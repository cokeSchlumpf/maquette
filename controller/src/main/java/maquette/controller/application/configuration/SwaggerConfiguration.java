package maquette.controller.application.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

@Configuration
@EnableSwagger2WebFlux
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(info())
            .enable(true)
            .select()
            .paths(PathSelectors.ant("/api/v1/**"))
            .build();
    }

    private ApiInfo info() {
        return new ApiInfoBuilder()
            .title("Maquette API")
            .description("The Maquette Controller API is very nice.")
            .version("0.0.42")
            .build();
    }

}
