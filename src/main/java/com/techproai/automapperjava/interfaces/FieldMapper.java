package com.techproai.automapperjava.interfaces;

import com.techproai.automapperjava.exceptions.MissingTypeException;
import com.techproai.automapperjava.exceptions.NoTypeConverterFoundException;

public interface FieldMapper {
    void map(Object i, Object o) throws NoTypeConverterFoundException, MissingTypeException;
}
