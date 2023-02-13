package com.tuana9a.automapperjava.converters;

import com.tuana9a.automapperjava.exceptions.MissingTypeException;
import com.tuana9a.automapperjava.exceptions.NoTypeConverterFoundException;
import com.tuana9a.automapperjava.exceptions.NoZeroArgumentsConstructorFoundException;
import com.tuana9a.automapperjava.interfaces.FieldMapper;
import com.tuana9a.automapperjava.interfaces.TypeConverter;
import com.tuana9a.automapperjava.mappers.SimpleFieldMapper;
import com.tuana9a.automapperjava.mappers.SimpleListFieldMapper;
import com.tuana9a.automapperjava.pools.TypeConverterPool;

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
            if (List.class.isAssignableFrom(inputField.getType())) {
                // if list can assign from field.type mean field.type is subclass of list, only subclass of list can assign to list
                fieldMappers.add(new SimpleListFieldMapper(inputField, outputField, typeConverterPool));
            } else {
                fieldMappers.add(new SimpleFieldMapper(inputField, outputField, typeConverterPool));
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
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
