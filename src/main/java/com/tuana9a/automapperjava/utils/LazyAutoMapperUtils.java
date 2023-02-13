package com.tuana9a.automapperjava.utils;

import com.tuana9a.automapperjava.exceptions.AutoMapperException;
import com.tuana9a.automapperjava.exceptions.GetTypeFromFirstElementFailedException;
import com.tuana9a.automapperjava.exceptions.NoZeroArgumentsConstructorFoundException;
import com.tuana9a.automapperjava.interfaces.AutoMapper;
import com.tuana9a.automapperjava.interfaces.TypeConverter;
import com.tuana9a.automapperjava.mappers.LazyObjectConverter;
import com.tuana9a.automapperjava.pools.TypeConverterPool;

import java.util.LinkedList;
import java.util.List;

public class LazyAutoMapperUtils implements AutoMapper {

    private final TypeConverterPool typeConverterPool;

    public LazyAutoMapperUtils(TypeConverterPool typeConverterPool) {
        this.typeConverterPool = typeConverterPool;
    }

    public <I, O> TypeConverter<I, O> add(Class<I> inputClass, Class<O> outputClass) throws NoZeroArgumentsConstructorFoundException {
        LazyObjectConverter<I, O> lazyObjectConverter = new LazyObjectConverter<>(inputClass, outputClass, typeConverterPool);
        typeConverterPool.add(inputClass, outputClass, x -> lazyObjectConverter.convert(x));
        return lazyObjectConverter;
    }

    @Override
    public <I, O> O convertIgnoreException(I input, Class<O> targetClass) {
        if (input == null) return null;
        try {
            TypeConverter<I, O> typeConverter = (TypeConverter<I, O>) this.typeConverterPool.get(input.getClass(), targetClass);
            if (typeConverter == null) {
                typeConverter = this.add((Class<I>) input.getClass(), targetClass);
                return typeConverter.convert(input);
            }
        } catch (AutoMapperException ignored) {
        }
        return null;
    }

    @Override
    public <I, O> O convert(I input, Class<O> targetClass) throws AutoMapperException {
        if (input == null) return null;
        TypeConverter<I, O> typeConverter = (TypeConverter<I, O>) this.typeConverterPool.get(input.getClass(), targetClass);
        if (typeConverter == null) {
            typeConverter = this.add((Class<I>) input.getClass(), targetClass);
        }
        return typeConverter.convert(input);
    }

    @Override
    public <I, O> List<O> convertList(List<I> inputs, Class<O> targetClass) throws AutoMapperException {
        if (inputs.size() == 0) return new LinkedList<>();
        I firstElement = inputs.get(0);
        if (firstElement == null) {
            throw new GetTypeFromFirstElementFailedException("First element is null");
        }
        TypeConverter<I, O> typeConverter = (TypeConverter<I, O>) this.typeConverterPool.get(firstElement.getClass(), targetClass);
        if (typeConverter == null) {
            typeConverter = this.add((Class<I>) firstElement.getClass(), targetClass);
        }
        List<O> outputs = new LinkedList<>();
        for (I i : inputs) {
            outputs.add(typeConverter.convert(i));
        }
        return outputs;
    }
}
