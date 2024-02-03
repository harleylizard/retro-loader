package com.harleylizard.retro.util;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowableConsumer<O, T extends Throwable> {

    void apply(O o) throws T;

    static <O, T extends Exception> Consumer<O> wrap(ThrowableConsumer<O, T> consumer) {
        return o -> {
            try {
                consumer.apply(o);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
