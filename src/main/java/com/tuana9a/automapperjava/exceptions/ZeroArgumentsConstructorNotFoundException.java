package com.tuana9a.automapperjava.exceptions;

public class ZeroArgumentsConstructorNotFoundException extends AutoMapperException {
    private final Class<?> klass;

    public ZeroArgumentsConstructorNotFoundException(Class<?> klass) {
        super("Zero arguments constructor not found for class " + klass.getName());
        this.klass = klass;
    }

    public Class<?> getKlass() {
        return klass;
    }
}
