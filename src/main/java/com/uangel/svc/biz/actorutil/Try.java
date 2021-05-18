package com.uangel.svc.biz.actorutil;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class Try<T> {

    private Try() {

    }

    public abstract <U> Try<U> map(Function<T,U> mapf);
    public abstract <U> Try<U> flatMap(Function<T,Try<U>> mapf);
    public abstract  Try<T> recoverWith( Function<Throwable,Try<T>> recoverFunc);

    public abstract boolean isSuccess();
    public abstract T get();
    public abstract Try<Throwable> failed();

    public T getOrElse(Supplier<T> supplier) {
        if (isSuccess()) {
            return get();
        } else {
            return supplier.get();
        }
    }

    public Optional<T> toOptional() {
        if (isSuccess()) {
            return Optional.of(get());
        } else {
            return Optional.empty();
        }
    }

    public CompletableFuture<T> toFuture() {
        if (isSuccess()) {
            return CompletableFuture.completedFuture(get());
        } else {
            return CompletableFuture.failedFuture(failed().get());
        }
    }

    public static <T> Try<T> Success(T value) {
        return new success<>(value);
    }

    public static <T> Try<T> Failure( Throwable e) {
        return new failure<>(e);
    }

    public static <T> Try<T> from(SupplierEx<T> supplier) {
        try {
            return Success(supplier.get());
        } catch ( Throwable e ) {
            return Failure(e);
        }
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalIsPresent"})
    public static <T> Try<T> fromOptional(Optional<T> opt) {
        if (opt.isPresent()) {
            return Success(opt.get());
        }
        return Failure(new NoSuchElementException("Option.empty"));
    }

    private static class failure<T> extends Try<T> {
        Throwable error;
        failure(Throwable v) {
            this.error = v;
        }
        @Override
        public <U> Try<U> map(Function<T, U> mapf) {
            return new failure<>(error);
        }

        @Override
        public <U> Try<U> flatMap(Function<T, Try<U>> mapf) {
            return new failure<>(error);
        }

        @Override
        public Try<T> recoverWith(Function<Throwable, Try<T>> recoverFunc) {
            return recoverFunc.apply(error);
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public T get() {
            throw new RuntimeException(error);
        }

        @Override
        public Try<Throwable> failed() {
            return new success<>(error);
        }
    }

    private static class success<T> extends Try<T> {

        T value;
        success(T v) {
            this.value = v;
        }

        @Override
        public <U> Try<U> map(Function<T, U> mapf) {
            return new success<>(mapf.apply(value));
        }

        @Override
        public <U> Try<U> flatMap(Function<T, Try<U>> mapf) {
            return mapf.apply(value);
        }

        @Override
        public Try<T> recoverWith(Function<Throwable, Try<T>> recoverFunc) {
            return this;
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Try<Throwable> failed() {
            return new failure<>(new UnsupportedOperationException("Success.failed") );
        }
    }
}
