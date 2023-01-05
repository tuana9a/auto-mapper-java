package com.techproai.automapperjava.exceptions;

public class NoTypeConverterFoundException extends AutoMapperException {
    public NoTypeConverterFoundException(String in, String out) {
        super(String.format("%s::%s", in, out));
    }
}
