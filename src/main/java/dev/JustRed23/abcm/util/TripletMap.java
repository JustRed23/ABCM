package dev.JustRed23.abcm.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record TripletMap<F, S, T>(F first, S second, T third) {

    @Contract("_, _, _ -> new")
    public static <F, S, T> @NotNull TripletMap<F, S, T> of(F first, S second, T third) {
        return new TripletMap<>(first, second, third);
    }
}
