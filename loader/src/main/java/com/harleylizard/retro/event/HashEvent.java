package com.harleylizard.retro.event;

import com.harleylizard.retro.Entrypoint;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class HashEvent<T> implements Event<T> {
    private final Map<Entrypoint, T> map = new HashMap<>();

    @Override
    public void subscribe(Entrypoint entrypoint, T t) {
        map.putIfAbsent(entrypoint, t);
    }

    @Override
    public void post(Consumer<T> consumer) {
        for (Map.Entry<Entrypoint, T> entry : map.entrySet()) {
            consumer.accept(entry.getValue());
        }
    }
}
