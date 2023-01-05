package com.techproai.automapperjava.interfaces;

import com.techproai.automapperjava.exceptions.NoTypeConverterFoundException;

public interface TypeConverter<I, O> {
    O apply(I i) throws NoTypeConverterFoundException;
}
