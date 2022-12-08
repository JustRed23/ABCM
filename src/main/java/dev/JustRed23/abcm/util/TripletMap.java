package dev.JustRed23.abcm.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class TripletMap<F, S, T> {

    private final F first;
    private final S second;
    private final T third;

    public TripletMap(F first, S second, T third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Contract("_, _, _ -> new")
    public static <F, S, T> @NotNull TripletMap<F, S, T> of(F first, S second, T third) {
        return new TripletMap<>(first, second, third);
    }

    public F first() {
        return first;
    }

    public S second() {
        return second;
    }

    public T third() {
        return third;
    }
}
