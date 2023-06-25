package maquette.infrastructure;

import lombok.extern.slf4j.Slf4j;
import maquette.common.Operators;
import maquette.core.domain.users.rbac.DomainRole;
import maquette.core.domain.users.rbac.EmptyRole;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Converter to map the name of a {@link DomainRole} to an actual instance.
 * <p>
 * The converter looks searches for all implementations of {@link DomainRole}
 * and compares the name of each role with the string to be mappped.
 * <p>
 * If no match is found it will return an {@link EmptyRole} as a fallback.
 * <p>
 * See also developer documentation: docs/development/rbac.md
 */
@Slf4j
@Component
@ConfigurationPropertiesBinding
public class DomainRoleSpringConfigurationConverter implements Converter<String, DomainRole> {

    private final Set<Class<DomainRole>> roleTypes;

    public DomainRoleSpringConfigurationConverter(
        ApplicationContext context
    ) {
        this.roleTypes = getApplicationDomainRoleTypes(context);
    }

    @Override
    public DomainRole convert(String source) {
        return this
            .roleTypes
            .stream()
            .<Optional<DomainRole>>map(cls -> {
                var nameField = Operators.ignoreExceptionToOptional(() -> cls.getField("NAME"));

                if (nameField.isEmpty() || !nameField.get().getType().equals(String.class)) {
                    return Optional.empty();
                }

                var name = Operators.suppressExceptions(() -> (String) nameField.get().get(null));

                if (source.startsWith(name)) {
                    var constructor = Operators.suppressExceptions(
                        () -> cls.getConstructor(),
                        "Default Domain Roles must implement default constructor."
                    );

                    return Optional.of(Operators.suppressExceptions(
                        () -> constructor.newInstance()
                    ));
                } else {
                    return Optional.empty();
                }
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElseGet(() -> {
                log.warn("Cannot map `{}` to a domain role.", source);

                return new EmptyRole();
            });
    }

    @SuppressWarnings("unchecked")
    private static Set<Class<DomainRole>> getApplicationDomainRoleTypes(
        ApplicationContext applicationContext
    ) {
        var packages = AutoConfigurationPackages.get(applicationContext.getAutowireCapableBeanFactory());
        var provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AssignableTypeFilter(DomainRole.class));

        Set<Class<DomainRole>> allRoles = new HashSet<>();

        for (String pkg : packages) {
            var candidates = provider.findCandidateComponents(pkg);

            var roles = candidates
                .stream()
                .map(beanDefinition -> {
                    try {
                        return (Class<DomainRole>) DomainRoleSpringConfigurationConverter
                            .class
                            .getClassLoader()
                            .loadClass(beanDefinition.getBeanClassName());
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toSet());

            allRoles.addAll(roles);
        }

        return allRoles;
    }

}
