package com.tuana9a.automapperjava.mappers;

import com.tuana9a.automapperjava.db.TypeConverterDb;
import com.tuana9a.automapperjava.db.TypeMapperDb;
import com.tuana9a.automapperjava.db.ZeroArgsConstructorDb;
import com.tuana9a.automapperjava.exceptions.MissingTypeException;
import com.tuana9a.automapperjava.exceptions.NoTypeMapperFoundException;
import com.tuana9a.automapperjava.exceptions.ZeroArgumentsConstructorNotFoundException;
import com.tuana9a.automapperjava.interfaces.FieldMapper;
import com.tuana9a.automapperjava.interfaces.TypeConverter;
import com.tuana9a.automapperjava.interfaces.TypeMapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class LazyObjectAutoMapper<I, O> implements TypeMapper<I, O>, TypeConverter<I, O> {
    private Class<O> outputClass;
    private List<FieldMapper> fieldMappers;
    private final TypeMapperDb typeMapperDb;
    private final TypeConverterDb typeConverterDb;
    private final ZeroArgsConstructorDb zeroArgsConstructorDb;

    public LazyObjectAutoMapper() {
        this(TypeMapperDb.getInstance(), TypeConverterDb.getInstance(), ZeroArgsConstructorDb.getInstance());
    }

    public LazyObjectAutoMapper(TypeMapperDb typeMapperDb, TypeConverterDb typeConverterDb, ZeroArgsConstructorDb zeroArgsConstructorDb) {
        this.typeMapperDb = typeMapperDb;
        this.typeConverterDb = typeConverterDb;
        this.zeroArgsConstructorDb = zeroArgsConstructorDb;
    }

    public LazyObjectAutoMapper<I, O> parse(Class<I> inputClass, Class<O> outputClass) throws ZeroArgumentsConstructorNotFoundException {
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
        keys.retainAll(outputFields.keySet()); // intersect to get same keys in both object

        for (String key : keys) {
            Field inputField = inputFields.get(key);
            Field outputField = outputFields.get(key);
            if (List.class.isAssignableFrom(inputField.getType())) { // if field can be assign to type List -> then it a child of List
                fieldMappers.add(new LazyListFieldMapper(typeMapperDb, typeConverterDb, zeroArgsConstructorDb).in(inputField).out(outputField));
            } else {
                fieldMappers.add(new LazyFieldMapper(typeMapperDb, typeConverterDb, zeroArgsConstructorDb).in(inputField).out(outputField));
            }
        }

        this.typeMapperDb.updateIfNotExist(inputClass, outputClass, this);
        this.typeConverterDb.updateIfNotExist(inputClass, outputClass, this);
        this.zeroArgsConstructorDb.updateIfNotExist(outputClass);
        return this;
    }

    @Override
    public O convert(I i) throws NoTypeMapperFoundException, MissingTypeException {
        if (i == null) return null;
        try {
            Constructor<O> outputZeroArgsConstructor = this.zeroArgsConstructorDb.get(outputClass);
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
