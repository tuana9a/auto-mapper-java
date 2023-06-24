package com.tuana9a.automapperjava.utils;

import com.tuana9a.automapperjava.db.TypeConverterDb;
import com.tuana9a.automapperjava.db.TypeMapperDb;
import com.tuana9a.automapperjava.db.ZeroArgsConstructorDb;
import com.tuana9a.automapperjava.exceptions.AutoMapperException;
import com.tuana9a.automapperjava.exceptions.FailedToGetFirstElementTypeException;
import com.tuana9a.automapperjava.exceptions.ZeroArgumentsConstructorNotFoundException;
import com.tuana9a.automapperjava.interfaces.AutoMapper;
import com.tuana9a.automapperjava.interfaces.TypeConverter;
import com.tuana9a.automapperjava.interfaces.TypeMapper;
import com.tuana9a.automapperjava.mappers.LazyObjectAutoMapper;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

public class LazyAutoMapperUtils implements AutoMapper {
    private static final LazyAutoMapperUtils INSTANCE = new LazyAutoMapperUtils();

    public static LazyAutoMapperUtils getInstance() {
        return INSTANCE;
    }

    @Getter
    private final TypeMapperDb typeMapperDb;

    @Getter
    private final TypeConverterDb typeConverterDb;

    @Getter
    private final ZeroArgsConstructorDb zeroArgsConstructorDb;

    public LazyAutoMapperUtils() {
        this(TypeMapperDb.getInstance(), TypeConverterDb.getInstance(), ZeroArgsConstructorDb.getInstance());
    }

    public LazyAutoMapperUtils(TypeMapperDb typeMapperDb, TypeConverterDb typeConverterDb, ZeroArgsConstructorDb zeroArgsConstructorDb) {
        this.typeMapperDb = typeMapperDb;
        this.typeConverterDb = typeConverterDb;
        this.zeroArgsConstructorDb = zeroArgsConstructorDb;
    }

    public <I, O> LazyObjectAutoMapper<I, O> add(Class<I> inputClass, Class<O> outputClass) throws ZeroArgumentsConstructorNotFoundException {
        return new LazyObjectAutoMapper<I, O>(typeMapperDb, typeConverterDb, zeroArgsConstructorDb).parse(inputClass, outputClass);
    }

    @Override
    public <I, O> O convertIgnoreException(I input, Class<O> targetClass) {
        if (input == null) return null;
        try {
            TypeConverter<I, O> converter = (TypeConverter<I, O>) this.typeConverterDb.get(input.getClass(), targetClass);
            if (converter == null) {
                converter = this.add((Class<I>) input.getClass(), targetClass);
                return converter.convert(input);
            }
        } catch (AutoMapperException ignored) {
        }
        return null;
    }

    @Override
    public <I, O> O convert(I input, Class<O> targetClass) throws AutoMapperException {
        if (input == null) return null;
        TypeConverter<I, O> converter = (TypeConverter<I, O>) this.typeConverterDb.get(input.getClass(), targetClass);
        if (converter == null) {
            converter = this.add((Class<I>) input.getClass(), targetClass);
        }
        return converter.convert(input);
    }

    @Override
    public <I, O> List<O> convertList(List<I> inputs, Class<O> targetClass) throws AutoMapperException {
        if (inputs.size() == 0) return new LinkedList<>();
        I firstElement = inputs.get(0);
        if (firstElement == null) {
            throw new FailedToGetFirstElementTypeException("First element is null");
        }
        TypeConverter<I, O> converter = (TypeConverter<I, O>) this.typeConverterDb.get(firstElement.getClass(), targetClass);
        if (converter == null) {
            converter = this.add((Class<I>) firstElement.getClass(), targetClass);
        }
        List<O> outputs = new LinkedList<>();
        for (I i : inputs) {
            outputs.add(converter.convert(i));
        }
        return outputs;
    }

    @Override
    public <I, O> O mapIgnoreException(I input, O output) {
        if (input == null) return null;
        Class targetClass = output.getClass();
        try {
            TypeMapper<I, O> mapper = (TypeMapper<I, O>) this.typeMapperDb.get(input.getClass(), targetClass);
            if (mapper == null) {
                mapper = this.add((Class<I>) input.getClass(), targetClass);
                return mapper.map(input, output);
            }
        } catch (AutoMapperException ignored) {
        }
        return null;
    }

    @Override
    public <I, O> O map(I input, O output) throws AutoMapperException {
        if (input == null) return null;
        Class targetClass = output.getClass();
        TypeMapper<I, O> mapper = (TypeMapper<I, O>) this.typeMapperDb.get(input.getClass(), targetClass);
        if (mapper == null) {
            mapper = this.add((Class<I>) input.getClass(), targetClass);
        }
        return mapper.map(input, output);
    }
}
