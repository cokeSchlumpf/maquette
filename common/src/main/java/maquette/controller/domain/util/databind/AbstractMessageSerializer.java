package maquette.controller.domain.util.databind;

import java.io.NotSerializableException;
import java.util.Map;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Suppliers;

import akka.actor.ExtendedActorSystem;
import akka.serialization.SerializerWithStringManifest;
import maquette.controller.domain.util.Operators;

public abstract class AbstractMessageSerializer extends SerializerWithStringManifest {

    private final ExtendedActorSystem system;

    private final int identifier;

    private final ObjectMapper om;

    private final Supplier<Map<String, Class<?>>> manifestToClass;

    protected AbstractMessageSerializer(ExtendedActorSystem actorSystem, int identifier) {
        this.system = actorSystem;
        this.om = ObjectMapperFactory.apply().create(true);
        this.manifestToClass = Suppliers.memoize(this::getManifestToClass);
        this.identifier = identifier;
    }

    @Override
    public int identifier() {
        return identifier;
    }

    @Override
    public String manifest(Object o) {
        return manifestToClass
            .get()
            .keySet()
            .stream()
            .filter(manifest -> manifestToClass.get().get(manifest).isInstance(o))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unknown message type " + o.getClass()));
    }

    @Override
    public byte[] toBinary(Object o) {
        system.log().debug("Serialize message of type {}", o.getClass().getName());
        return Operators.suppressExceptions(() -> om.writeValueAsBytes(o));
    }

    @Override
    public Object fromBinary(byte[] bytes, String manifest) throws NotSerializableException {
        Class<?> target = manifestToClass.get().get(manifest);

        system.log().debug("Deserialize message for manifest {}", manifest);

        if (target == null) {
            throw new NotSerializableException("Unknown manifest " + manifest);
        }

        return Operators.suppressExceptions(() -> om.readValue(bytes, target));
    }

    protected abstract Map<String, Class<?>> getManifestToClass();

}
