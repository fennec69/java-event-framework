package com.fennec.service;

/**
 * Created by fennec on 11/01/2016.
 */
public interface Filter<T extends Event> {
    boolean apply(T event);

    boolean equals(Filter filter);
}
