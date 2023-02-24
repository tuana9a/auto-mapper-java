package com.tuana9a.automapperjava.auto;

import com.tuana9a.automapperjava.db.TypeConverterDb;
import com.tuana9a.automapperjava.db.TypeMapperDb;
import com.tuana9a.automapperjava.db.ZeroArgsConstructorDb;
import com.tuana9a.automapperjava.exceptions.MissingTypeException;
import com.tuana9a.automapperjava.exceptions.NoTypeMapperFoundException;
import com.tuana9a.automapperjava.exceptions.ZeroArgumentsConstructorNotFoundException;
import com.tuana9a.automapperjava.interfaces.FieldMapper;
import com.tuana9a.automapperjava.interfaces.TypeConverter;
import com.tuana9a.automapperjava.interfaces.TypeMapper;
import com.tuana9a.automapperjava.mappers.LazyFieldMapper;
import com.tuana9a.automapperjava.mappers.LazyListFieldMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class LazyObjectAutoMapper<I, O> implements TypeMapper<I, O>, TypeConverter<I, O> {
    private final Class<O> outputClass;
    private final List<FieldMapper> fieldMappers;

    public LazyObjectAutoMapper(Class<I> inputClass, Class<O> outputClass) throws ZeroArgumentsConstructorNotFoundException {
        this.outputClass = outputClass;
        this.fieldMappers = new LinkedList<>();

        Map<String, Field> inputFields = new HashMap<>();
        for (Field field : inputClass.getDeclaredFields()) {
            inputFields.put(field.getName(), field);
        }

        Map<String, Field> outputFields = new HashMap<>();
        for (Field field : outputClass.getDeclaredFields()) {
            outputFields.put(field.getName(), field);
        }

        Set<String> keys = inputFields.keySet();
        keys.retainAll(outputFields.keySet()); // intersect

        for (String key : keys) {
            Field inputField = inputFields.get(key);
            Field outputField = outputFields.get(key);
            if (inputField.getType().isAssignableFrom(List.class)) {
                fieldMappers.add(new LazyListFieldMapper(inputField, outputField));
            } else {
                fieldMappers.add(new LazyFieldMapper(inputField, outputField));
            }
        }

        TypeMapperDb.getInstance().updateIfNotExist(inputClass, outputClass, this);
        TypeConverterDb.getInstance().updateIfNotExist(inputClass, outputClass, this);
        ZeroArgsConstructorDb.getInstance().updateIfNotExist(outputClass);
    }

    @Override
    public O convert(I i) throws NoTypeMapperFoundException, MissingTypeException {
        if (i == null) return null;
        try {
            Constructor<O> outputZeroArgsConstructor = ZeroArgsConstructorDb.getInstance().get(outputClass);
            O o = outputZeroArgsConstructor.newInstance();
            this.map(i, o);
            return o;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public O map(I i, O o) throws MissingTypeException, NoTypeMapperFoundException {
        if (i == null) return null;
        for (FieldMapper fieldMapper : fieldMappers) {
            fieldMapper.map(i, o);
        }
        return o;
    }
}
