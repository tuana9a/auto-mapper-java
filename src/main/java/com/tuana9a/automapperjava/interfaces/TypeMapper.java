package com.tuana9a.automapperjava.interfaces;

import com.tuana9a.automapperjava.exceptions.MissingTypeException;
import com.tuana9a.automapperjava.exceptions.NoTypeMapperFoundException;

public interface TypeMapper<I, O> {
    O map(I i, O o) throws MissingTypeException, NoTypeMapperFoundException;
}
