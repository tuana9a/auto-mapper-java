package com.tuana9a.automapperjava.interfaces;

import com.tuana9a.automapperjava.exceptions.MissingTypeException;
import com.tuana9a.automapperjava.exceptions.NoTypeConverterFoundException;

public interface TypeConverter<I, O> {
    O convert(I i) throws NoTypeConverterFoundException, MissingTypeException;
}
