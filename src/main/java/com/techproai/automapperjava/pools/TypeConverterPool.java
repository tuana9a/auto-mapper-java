package com.techproai.automapperjava.pools;

import com.techproai.automapperjava.common.AutoMapperConstants;
import com.techproai.automapperjava.interfaces.TypeConverter;

import java.util.HashMap;
import java.util.Map;

public class TypeConverterPool {
    private final String delimiter;

    private final Map<String, TypeConverter<?, ?>> pool;

    public TypeConverterPool(String delimiter) {
        this.delimiter = delimiter;
        this.pool = new HashMap<>();
    }

    public TypeConverterPool() {
        this(AutoMapperConstants.DEFAULT_DELIMITER);
    }

    public <I, O> void add(Class<I> iClass, Class<O> oClass, TypeConverter<I, O> typeConverter) {
        this.pool.put(iClass.getName() + delimiter + oClass.getName(), typeConverter);
    }

    public TypeConverter<?, ?> get(String iClass, String oClass) {
        return this.pool.get(iClass + delimiter + oClass);
    }

    public TypeConverter<?, ?> get(Class<?> iClass, Class<?> oClass) {
        return this.pool.get(iClass.getName() + delimiter + oClass.getName());
    }
}
