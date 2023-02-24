package com.tuana9a.automapperjava.interfaces;

import com.tuana9a.automapperjava.exceptions.MissingTypeException;
import com.tuana9a.automapperjava.exceptions.NoTypeMapperFoundException;

public interface TypeConverter<I, O> {
    O convert(I i) throws NoTypeMapperFoundException, MissingTypeException;
}
