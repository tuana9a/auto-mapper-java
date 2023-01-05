package com.techproai.automapperjava.exceptions;

public class NoZeroArgumentsConstructorFoundException extends AutoMapperException {
    private final Class<?> klass;

    public NoZeroArgumentsConstructorFoundException(Class<?> klass) {
        super("No zero argument constructor found for class " + klass.getName());
        this.klass = klass;
    }

    public Class<?> getKlass() {
        return klass;
    }
}
