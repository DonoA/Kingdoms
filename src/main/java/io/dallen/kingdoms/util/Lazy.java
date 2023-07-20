package io.dallen.kingdoms.util;

import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class Lazy<T> {

    public T v;
    public final Supplier<T> supplier;

    public T get() {
        if (v == null) {
            v = supplier.get();
        }

        return v;
    }
}
