package com.techproai.automapperjava.mappers;

import com.techproai.automapperjava.exceptions.MissingTypeException;
import com.techproai.automapperjava.exceptions.NoTypeConverterFoundException;
import com.techproai.automapperjava.exceptions.NoZeroArgumentsConstructorFoundException;
import com.techproai.automapperjava.interfaces.FieldMapper;
import com.techproai.automapperjava.interfaces.TypeConverter;
import com.techproai.automapperjava.pools.TypeConverterPool;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class LazyObjectConverter<I, O> implements TypeConverter<I, O> {
    private final List<FieldMapper> fieldMappers;
    private final Constructor<O> outputZeroArgsConstructor;

    public LazyObjectConverter(Class<I> inputClass, Class<O> outputClass, TypeConverterPool typeConverterPool) throws NoZeroArgumentsConstructorFoundException {
        Map<String, Field> inputFields = new HashMap<>();
        Map<String, Field> outputFields = new HashMap<>();

        this.fieldMappers = new LinkedList<>();
        this.outputZeroArgsConstructor = (Constructor<O>) Arrays.stream(outputClass.getConstructors()).filter(x -> x.getParameters().length == 0).findFirst().orElseThrow(() -> new NoZeroArgumentsConstructorFoundException(outputClass));

        for (Field field : inputClass.getDeclaredFields()) {
            inputFields.put(field.getName(), field);
        }

        for (Field field : outputClass.getDeclaredFields()) {
            outputFields.put(field.getName(), field);
        }

        Set<String> keys = inputFields.keySet();
        keys.retainAll(outputFields.keySet()); // intersect

        for (String key : keys) {
            Field inputField = inputFields.get(key);
            Field outputField = outputFields.get(key);
            if (inputField.getType().isAssignableFrom(List.class)) {
                fieldMappers.add(new LazyListFieldMapper(inputField, outputField, typeConverterPool));
            } else {
                fieldMappers.add(new LazyFieldMapper(inputField, outputField, typeConverterPool));
            }
        }
    }

    @Override
    public O convert(I i) throws NoTypeConverterFoundException, MissingTypeException {
        if (i == null) return null;
        try {
            O o = outputZeroArgsConstructor.newInstance();
            for (FieldMapper fieldMapper : fieldMappers) {
                fieldMapper.map(i, o);
            }
            return o;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
