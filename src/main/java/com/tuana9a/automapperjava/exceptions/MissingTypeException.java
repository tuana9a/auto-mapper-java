package com.tuana9a.automapperjava.exceptions;

public class MissingTypeException extends AutoMapperException {
    public MissingTypeException(String path1, String path2) {
        super(path1 + " or " + path2 + " is missing generic type");
    }
}
