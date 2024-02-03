package com.harleylizard.retro.event;

import com.harleylizard.retro.Entrypoint;

import java.util.function.Consumer;

public interface Event<T> {

    void subscribe(Entrypoint entrypoint, T t);

    void post(Consumer<T> consumer);
}
