package com.techproai.automapperjava.interfaces;

import com.techproai.automapperjava.exceptions.NoTypeConverterFoundException;

public interface FieldMapper {
    void apply(Object i, Object o) throws NoTypeConverterFoundException;
}
