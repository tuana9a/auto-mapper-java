package com.techproai.automapperjava.interfaces;

import com.techproai.automapperjava.exceptions.AutoMapperException;

import java.util.List;

public interface AutoMapper {
    <I, O> O convertIgnoreException(I input, Class<O> targetClass);

    <I, O> O convert(I input, Class<O> targetClass) throws AutoMapperException;

    <I, O> List<O> convertList(List<I> input, Class<O> targetClass) throws AutoMapperException;
}
