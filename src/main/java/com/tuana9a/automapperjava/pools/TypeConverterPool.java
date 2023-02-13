package com.tuana9a.automapperjava.pools;

import com.tuana9a.automapperjava.common.AutoMapperConstants;
import com.tuana9a.automapperjava.interfaces.TypeConverter;

import java.util.HashMap;
import java.util.Map;

public class TypeConverterPool {
    private final String delimiter;

    private final Map<String, TypeConverter> pool;

    public TypeConverterPool(String delimiter) {
        this.delimiter = delimiter;
        this.pool = new HashMap<>();
    }

    public TypeConverterPool() {
        this(AutoMapperConstants.DELIMITER);
    }

    public <I, O> void add(Class<I> inputClass, Class<O> outputClass, TypeConverter<I, O> typeConverter) {
        this.pool.put(String.join(delimiter, inputClass.getName(), outputClass.getName()), typeConverter);
    }

    public <I, O> void add(String inputClass, String outputClass, TypeConverter<I, O> typeConverter) {
        this.pool.put(String.join(delimiter, inputClass, outputClass), typeConverter);
    }

    public <I, O> TypeConverter<I, O> get(String inputClass, String outputClass) {
        TypeConverter typeConverter = this.pool.get(String.join(delimiter, inputClass, outputClass));
        if (typeConverter != null) return typeConverter;
        typeConverter = this.pool.get(String.join(delimiter, inputClass, AutoMapperConstants.ASTERISK));
        if (typeConverter != null) return typeConverter;
        typeConverter = this.pool.get(String.join(delimiter, AutoMapperConstants.ASTERISK, outputClass));
        return typeConverter;
    }

    public <I, O> TypeConverter<I, O> get(Class<I> inputClass, Class<O> outputClass) {
        return this.get(inputClass.getName(), outputClass.getName());
    }
}
