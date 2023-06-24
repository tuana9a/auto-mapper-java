package com.tuana9a.automapperjava.db;

import com.tuana9a.automapperjava.configs.AutoMapperConstants;
import com.tuana9a.automapperjava.interfaces.TypeMapper;

import java.util.HashMap;
import java.util.Map;

public class TypeMapperDb {
    private final String delimiter;

    private final Map<String, TypeMapper> db;

    private static final TypeMapperDb INSTANCE = new TypeMapperDb();

    public static TypeMapperDb getInstance() {
        return INSTANCE;
    }

    public TypeMapperDb(String delimiter) {
        this.delimiter = delimiter;
        this.db = new HashMap<>();
    }

    public TypeMapperDb() {
        this(AutoMapperConstants.DELIMITER);
    }

    public <I, O> void updateIfNotExist(Class<I> inputClass, Class<O> outputClass, TypeMapper<I, O> typeMapper) {
        if (!ifExist(inputClass, outputClass)) {
            this.update(inputClass, outputClass, typeMapper);
        }
    }

    public <I, O> boolean ifExist(Class<I> inputClass, Class<O> outputClass) {
        return this.get(inputClass, outputClass) != null;
    }

    public <I, O> void update(Class<I> inputClass, Class<O> outputClass, TypeMapper<I, O> typeMapper) {
        this.db.put(String.join(delimiter, inputClass.getName(), outputClass.getName()), typeMapper);
    }

    public <I, O> void update(String inputClass, String outputClass, TypeMapper<I, O> typeMapper) {
        this.db.put(String.join(delimiter, inputClass, outputClass), typeMapper);
    }

    public <I, O> TypeMapper<I, O> get(String inputClass, String outputClass) {
        TypeMapper typeMapper = this.db.get(String.join(delimiter, inputClass, outputClass));
        if (typeMapper != null) return typeMapper;
        typeMapper = this.db.get(String.join(delimiter, inputClass, AutoMapperConstants.ASTERISK));
        if (typeMapper != null) return typeMapper;
        typeMapper = this.db.get(String.join(delimiter, AutoMapperConstants.ASTERISK, outputClass));
        return typeMapper;
    }

    public <I, O> TypeMapper<I, O> get(Class<I> inputClass, Class<O> outputClass) {
        return this.get(inputClass.getName(), outputClass.getName());
    }
}
