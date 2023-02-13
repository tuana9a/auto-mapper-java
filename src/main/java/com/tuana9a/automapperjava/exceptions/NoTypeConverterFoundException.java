package com.tuana9a.automapperjava.exceptions;

public class NoTypeConverterFoundException extends AutoMapperException {
    public NoTypeConverterFoundException(String in, String out) {
        super(String.format("%s::%s", in, out));
    }
}
