package com.techproai.automapperjava.converters;

import com.techproai.automapperjava.exceptions.NoTypeConverterFoundException;
import com.techproai.automapperjava.exceptions.NoZeroArgumentsConstructorFoundException;
import com.techproai.automapperjava.interfaces.FieldMapper;
import com.techproai.automapperjava.interfaces.TypeConverter;
import com.techproai.automapperjava.mappers.SimpleFieldMapper;
import com.techproai.automapperjava.mappers.TypeListFieldMapper;
import com.techproai.automapperjava.pools.TypeConverterPool;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class SimpleObjectConverter<I, O> implements TypeConverter<I, O> {
    private final List<FieldMapper> fieldMappers;
    private final Constructor<O> outputZeroArgsConstructor;

    public SimpleObjectConverter(Class<I> inputClass, Class<O> outputClass, TypeConverterPool typeConverterPool) throws NoZeroArgumentsConstructorFoundException {
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
                fieldMappers.add(new TypeListFieldMapper(inputField, outputField, typeConverterPool));
            } else {
                fieldMappers.add(new SimpleFieldMapper(inputField, outputField, typeConverterPool));
            }
        }
    }

    @Override
    public O apply(I i) throws NoTypeConverterFoundException {
        if (i == null) return null;
        O o;
        try {
            o = outputZeroArgsConstructor.newInstance();
            for (FieldMapper fieldMapper : fieldMappers) {
                fieldMapper.apply(i, o);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return o;
    }
}
