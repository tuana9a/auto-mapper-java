package com.techproai.automapperjava.utils;

import com.techproai.automapperjava.converters.SimpleObjectConverter;
import com.techproai.automapperjava.exceptions.GetTypeFromFirstElementFailedException;
import com.techproai.automapperjava.exceptions.NoTypeConverterFoundException;
import com.techproai.automapperjava.exceptions.NoZeroArgumentsConstructorFoundException;
import com.techproai.automapperjava.interfaces.AutoMapper;
import com.techproai.automapperjava.interfaces.TypeConverter;
import com.techproai.automapperjava.pools.TypeConverterPool;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * use {@link LazyAutoMapperUtils} instead
 */
@Deprecated
public class AutoMapperUtils implements AutoMapper {

    private final TypeConverterPool typeConverterPool;

    public AutoMapperUtils(TypeConverterPool typeConverterPool) {
        this.typeConverterPool = typeConverterPool;
    }

    public <I, O> void add(Class<I> inputClass, Class<O> outputClass) throws NoZeroArgumentsConstructorFoundException {
        SimpleObjectConverter<I, O> simpleObjectConverter = new SimpleObjectConverter(inputClass, outputClass, typeConverterPool);
        typeConverterPool.add(inputClass, outputClass, x -> simpleObjectConverter.convert(x));
    }

    @Override
    public <I, O> O convertIgnoreException(I input, Class<O> targetClass) {
        TypeConverter<I, O> typeConverter = (TypeConverter<I, O>) this.typeConverterPool.get(input.getClass(), targetClass);
        if (typeConverter == null) return null;
        try {
            return typeConverter.convert(input);
        } catch (NoTypeConverterFoundException e) {
            return null;
        }
    }

    @Override
    public <I, O> O convert(I input, Class<O> targetClass) throws NoTypeConverterFoundException {
        if (input == null) return null;
        TypeConverter<I, O> typeConverter = (TypeConverter<I, O>) this.typeConverterPool.get(input.getClass(), targetClass);
        if (typeConverter == null) {
            throw new NoTypeConverterFoundException(input.getClass().getName(), targetClass.getName());
        }
        return typeConverter.convert(input);
    }

    @Override
    public <I, O> List<O> convertList(List<I> input, Class<O> targetClass) throws NoTypeConverterFoundException, GetTypeFromFirstElementFailedException {
        if (input.size() == 0) return new LinkedList<>();
        I firstElement = input.get(0);
        if (firstElement == null) {
            throw new GetTypeFromFirstElementFailedException("first element is null");
        }
        TypeConverter<I, O> typeConverter = (TypeConverter<I, O>) this.typeConverterPool.get(firstElement.getClass(), targetClass);
        if (typeConverter == null) {
            throw new NoTypeConverterFoundException(firstElement.getClass().getName(), targetClass.getName());
        }
        return input.stream().map(x -> {
            try {
                return typeConverter.convert(x);
            } catch (NoTypeConverterFoundException ignored) {
            }
            return null;
        }).collect(Collectors.toList());
    }
}
