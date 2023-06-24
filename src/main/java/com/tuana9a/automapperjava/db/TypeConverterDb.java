package com.tuana9a.automapperjava.db;

import com.tuana9a.automapperjava.configs.AutoMapperConstants;
import com.tuana9a.automapperjava.interfaces.TypeConverter;

import java.util.HashMap;
import java.util.Map;

public class TypeConverterDb {
    private final String delimiter;

    private final Map<String, TypeConverter> db;

    private static final TypeConverterDb INSTANCE = new TypeConverterDb();

    public static TypeConverterDb getInstance() {
        return INSTANCE;
    }

    public TypeConverterDb(String delimiter) {
        this.delimiter = delimiter;
        this.db = new HashMap<>();
    }

    public TypeConverterDb() {
        this(AutoMapperConstants.DELIMITER);
    }

    public <I, O> void update(Class<I> inputClass, Class<O> outputClass, TypeConverter<I, O> typeConverter) {
        this.db.put(String.join(delimiter, inputClass.getName(), outputClass.getName()), typeConverter);
    }

    public <I, O> void update(String inputClass, String outputClass, TypeConverter<I, O> typeConverter) {
        this.db.put(String.join(delimiter, inputClass, outputClass), typeConverter);
    }

    public <I, O> void updateIfNotExist(Class<I> inputClass, Class<O> outputClass, TypeConverter<I, O> typeConverter) {
        if (!ifExist(inputClass, outputClass)) {
            this.update(inputClass, outputClass, typeConverter);
        }
    }

    public <I, O> boolean ifExist(Class<I> inputClass, Class<O> outputClass) {
        return this.get(inputClass, outputClass) != null;
    }

    public <I, O> TypeConverter<I, O> get(String inputClass, String outputClass) {
        TypeConverter typeConverter = this.db.get(String.join(delimiter, inputClass, outputClass));
        if (typeConverter != null) return typeConverter;
        typeConverter = this.db.get(String.join(delimiter, inputClass, AutoMapperConstants.ASTERISK));
        if (typeConverter != null) return typeConverter;
        typeConverter = this.db.get(String.join(delimiter, AutoMapperConstants.ASTERISK, outputClass));
        return typeConverter;
    }

    public <I, O> TypeConverter<I, O> get(Class<I> inputClass, Class<O> outputClass) {
        return this.get(inputClass.getName(), outputClass.getName());
    }
}
