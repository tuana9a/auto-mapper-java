package com.techproai.automapperjava.utils;

import com.techproai.automapperjava.converters.SimpleObjectConverter;
import com.techproai.automapperjava.exceptions.GetTypeFromFirstElementFailedException;
import com.techproai.automapperjava.exceptions.NoTypeConverterFoundException;
import com.techproai.automapperjava.exceptions.NoZeroArgumentsConstructorFoundException;
import com.techproai.automapperjava.interfaces.TypeConverter;
import com.techproai.automapperjava.pools.TypeConverterPool;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AutoMapperUtils {

    private final TypeConverterPool typeConverterPool;

    public AutoMapperUtils(TypeConverterPool typeConverterPool) {
        this.typeConverterPool = typeConverterPool;
    }

    public void add(Class inputClass, Class outputClass) {
        try {
            SimpleObjectConverter simpleObjectConverter = new SimpleObjectConverter(inputClass, outputClass, typeConverterPool);
            typeConverterPool.add(inputClass, outputClass, x -> simpleObjectConverter.apply(x));
        } catch (NoZeroArgumentsConstructorFoundException e) {
            e.printStackTrace();
        }
    }

    public <I, O> O convertIgnoreException(I input, Class<O> targetClass) {
        TypeConverter<I, O> typeConverter = (TypeConverter<I, O>) this.typeConverterPool.get(input.getClass(), targetClass);
        if (typeConverter == null) return null;
        try {
            return typeConverter.apply(input);
        } catch (NoTypeConverterFoundException e) {
            return null;
        }
    }

    public <I, O> O convert(I input, Class<O> targetClass) throws NoTypeConverterFoundException {
        if (input == null) return null;
        TypeConverter<I, O> typeConverter = (TypeConverter<I, O>) this.typeConverterPool.get(input.getClass(), targetClass);
        if (typeConverter == null) {
            throw new NoTypeConverterFoundException(input.getClass().getName(), targetClass.getName());
        }
        return typeConverter.apply(input);
    }

    public <I, O> List<O> convertList(List<I> input, Class<O> targetClass) throws NoTypeConverterFoundException, GetTypeFromFirstElementFailedException {
        if (input.size() == 0) return new LinkedList<>();
        I firstElement = input.get(0);
        if (firstElement == null) {
            throw new GetTypeFromFirstElementFailedException("first element is null");
        }
        TypeConverter<I, O> typeConverter = (TypeConverter<I, O>) this.typeConverterPool.get(firstElement.getClass(), targetClass);
        if (typeConverter == null) {
            throw new NoTypeConverterFoundException(input.getClass().getName(), targetClass.getName());
        }
        return input.stream().map(x -> {
            try {
                return typeConverter.apply(x);
            } catch (NoTypeConverterFoundException ignored) {
            }
            return null;
        }).collect(Collectors.toList());
    }
}
