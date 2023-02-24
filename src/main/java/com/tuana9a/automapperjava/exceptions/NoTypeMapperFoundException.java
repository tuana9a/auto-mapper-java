package com.tuana9a.automapperjava.exceptions;

public class NoTypeMapperFoundException extends AutoMapperException {
    public NoTypeMapperFoundException(String in, String out) {
        super(String.format("%s::%s", in, out));
    }
}
