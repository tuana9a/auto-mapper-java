package com.techproai.automapperjava.utils;

import com.techproai.automapperjava.exceptions.AutoMapperException;
import com.techproai.automapperjava.exceptions.GetTypeFromFirstElementFailedException;
import com.techproai.automapperjava.exceptions.NoTypeConverterFoundException;
import com.techproai.automapperjava.exceptions.NoZeroArgumentsConstructorFoundException;
import com.techproai.automapperjava.interfaces.AutoMapper;
import com.techproai.automapperjava.interfaces.TypeConverter;
import com.techproai.automapperjava.mappers.LazyObjectConverter;
import com.techproai.automapperjava.pools.TypeConverterPool;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
    public <I, O> List<O> convertList(List<I> input, Class<O> targetClass) throws AutoMapperException {
        if (input.size() == 0) return new LinkedList<>();
        I firstElement = input.get(0);
        if (firstElement == null) {
            throw new GetTypeFromFirstElementFailedException("first element is null");
        }
        TypeConverter<I, O> typeConverter = (TypeConverter<I, O>) this.typeConverterPool.get(firstElement.getClass(), targetClass);
        if (typeConverter == null) {
            typeConverter = this.add((Class<I>) firstElement.getClass(), targetClass);
        }
        TypeConverter<I, O> finalTypeConverter = typeConverter;
        return input.stream().map(x -> {
            try {
                return finalTypeConverter.convert(x);
            } catch (AutoMapperException ignored) {
            }
            return null;
        }).collect(Collectors.toList());
    }
}
