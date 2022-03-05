package dev.projectg.crossplatforms.config;

public interface Message<T> {

    void send();

    T get();
}
