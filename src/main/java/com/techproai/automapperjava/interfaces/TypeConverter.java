package com.techproai.automapperjava.interfaces;

import com.techproai.automapperjava.exceptions.NoTypeConverterFoundException;

public interface TypeConverter<I, O> {
    O convert(I i) throws NoTypeConverterFoundException;
}
