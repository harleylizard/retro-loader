package com.harleylizard.retro.util;

import java.util.function.Predicate;

@FunctionalInterface
public interface ThrowablePredicate<J, T extends Throwable> {

    boolean test(J j) throws T;

    static <J, T extends Exception> Predicate<J> wrap(ThrowablePredicate<J, T> predicate) {
        return j -> {
            try {
                return predicate.test(j);
            } catch (Exception e) {
                return false;
            }
        };
    }
}