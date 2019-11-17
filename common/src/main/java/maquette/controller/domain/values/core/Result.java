package maquette.controller.domain.values.core;

import java.util.function.Consumer;
import java.util.function.Function;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Result.Success.class, name = "success"),
    @JsonSubTypes.Type(value = Result.Failure.class, name = "failure")
})
public abstract class Result<L> {

    private Result() {
    }

    public static <L> Result<L> success(L left) {
        return Success.apply(left);
    }

    public static <L> Result<L> failure(ErrorMessage right) {
        return Failure.apply(right);
    }

    public abstract <T> T map(Function<? super L, ? extends T> lFunc, Function<ErrorMessage, ? extends T> rFunc);

    public abstract void apply(Consumer<? super L> lFunc, Consumer<ErrorMessage> rFunc);

    private <T> Function<T, Void> consume(Consumer<T> c) {
        return t -> {
            c.accept(t);
            return null;
        };
    }

    @JsonIgnore
    public abstract boolean isSuccess();

    @JsonIgnore
    public abstract boolean isFailure();

    @Value
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Success<L> extends Result<L> {

        private L value;

        @JsonCreator
        private static <L> Success<L> apply(@JsonProperty("value") L value) {
            return new Success<>(value);
        }

        @Override
        public <T> T map(Function<? super L, ? extends T> lFunc, Function<ErrorMessage, ? extends T> rFunc) {
            return lFunc.apply(value);
        }

        @Override
        public void apply(Consumer<? super L> lFunc, Consumer<ErrorMessage> rFunc) {
            lFunc.accept(value);
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

    }

    @Value
    @EqualsAndHashCode(callSuper = false)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Failure<L> extends Result<L> {

        private ErrorMessage value;

        @JsonCreator
        private static <L> Failure<L> apply(@JsonProperty("value") ErrorMessage value) {
            return new Failure<>(value);
        }

        @Override
        public <T> T map(Function<? super L, ? extends T> lFunc, Function<ErrorMessage, ? extends T> rFunc) {
            return rFunc.apply(value);
        }

        @Override
        public void apply(Consumer<? super L> lFunc, Consumer<ErrorMessage> rFunc) {
            rFunc.accept(value);
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

    }

}
