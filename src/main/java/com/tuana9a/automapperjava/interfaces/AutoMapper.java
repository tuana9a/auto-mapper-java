package com.tuana9a.automapperjava.interfaces;

import com.tuana9a.automapperjava.exceptions.AutoMapperException;

import java.util.List;

public interface AutoMapper {
    <I, O> O convertIgnoreException(I input, Class<O> targetClass);

    <I, O> O convert(I input, Class<O> targetClass) throws AutoMapperException;

    <I, O> List<O> convertList(List<I> input, Class<O> targetClass) throws AutoMapperException;

    <I, O> O mapIgnoreException(I input, O output);

    <I, O> O map(I input, O output) throws AutoMapperException;
}
