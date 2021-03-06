package maquette.controller.domain.util;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;

import akka.japi.function.Function2;

public final class Operators {

    private static final Logger LOG = LoggerFactory.getLogger(Operators.class);

    private Operators() {

    }

    public static <T1, T2, R> CompletionStage<R> compose(
        CompletionStage<T1> cs1, CompletionStage<T2> cs2, Function2<T1, T2, R> combineWith) {
        CompletableFuture<T1> f1 = cs1.toCompletableFuture();
        CompletableFuture<T2> f2 = cs2.toCompletableFuture();

        return CompletableFuture
            .allOf(f1, f2)
            .thenApply(v -> Operators.suppressExceptions(() -> combineWith.apply(f1.join(), f2.join())));
    }

    public static <T, E extends Exception> CompletionStage<T> completeExceptionally(E with) {
        CompletableFuture<T> result = new CompletableFuture<>();
        result.completeExceptionally(with);
        return result;
    }

    public static <T> CompletionStage<T> completeExceptionally() {
        CompletableFuture<T> result = new CompletableFuture<>();
        result.completeExceptionally(new RuntimeException());
        return result;
    }

    public static <T> Optional<T> exceptionToNone(ExceptionalSupplier<T> supplier) {
        try {
            return Optional.of(supplier.get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static String hash() {
        return Hashing
            .goodFastHash(8)
            .newHasher()
            .putLong(System.currentTimeMillis())
            .putString(UUID.randomUUID().toString(), StandardCharsets.UTF_8)
            .hash()
            .toString();
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> hasCause(Throwable t, Class<T> exType) {
        if (exType.isInstance(t)) {
            return Optional.of((T) t);
        } else if (t.getCause() != null) {
            return hasCause(t.getCause(), exType);
        } else {
            return Optional.empty();
        }
    }

    public static String extractMessage(Throwable ex) {
        return Optional
            .ofNullable(ExceptionUtils.getRootCause(ex))
            .map(t -> String.format("%s: %s", t.getClass().getSimpleName(), t.getMessage()))
            .orElse(Optional
                        .ofNullable(ex.getMessage())
                        .map(str -> String.format("%s: %s", ex.getClass().getSimpleName(), ex.getMessage()))
                        .orElse(String.format("%s: No details provided.", ex.getClass().getSimpleName())));
    }

    public static void ignoreExceptions(ExceptionalRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            ExceptionUtils.wrapAndThrow(e);
        }
    }

    public static void suppressExceptions(ExceptionalRunnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            ExceptionUtils.wrapAndThrow(e);
        }
    }

    public static <T> T suppressExceptions(ExceptionalSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return ExceptionUtils.wrapAndThrow(e);
        }
    }

    @FunctionalInterface
    public interface ExceptionalRunnable {

        void run() throws Exception;

    }

    @FunctionalInterface
    public interface ExceptionalSupplier<T> {

        T get() throws Exception;

    }

    @FunctionalInterface
    public interface ExceptionalFunction<I, R> {

        R apply(I in) throws Exception;

    }

}
