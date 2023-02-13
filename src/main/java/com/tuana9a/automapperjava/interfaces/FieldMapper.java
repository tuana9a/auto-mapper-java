package com.tuana9a.automapperjava.interfaces;

import com.tuana9a.automapperjava.exceptions.MissingTypeException;
import com.tuana9a.automapperjava.exceptions.NoTypeConverterFoundException;

public interface FieldMapper {
    void map(Object i, Object o) throws NoTypeConverterFoundException, MissingTypeException;
}
