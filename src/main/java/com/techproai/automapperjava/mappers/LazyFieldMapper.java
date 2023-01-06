package com.techproai.automapperjava.mappers;

import com.techproai.automapperjava.exceptions.NoTypeConverterFoundException;
import com.techproai.automapperjava.exceptions.NoZeroArgumentsConstructorFoundException;
import com.techproai.automapperjava.interfaces.FieldMapper;
import com.techproai.automapperjava.interfaces.TypeConverter;
import com.techproai.automapperjava.options.MapperOpts;
import com.techproai.automapperjava.pools.TypeConverterPool;

import java.lang.reflect.Field;

public class LazyFieldMapper implements FieldMapper {
    private final Field inputField;
    private final Field outputField;
    private final MapperOpts mapperOpts;
    private final TypeConverterPool typeConverterPool;

    public LazyFieldMapper(Field in, Field out, TypeConverterPool typeConverterPool) {
        this(in, out, typeConverterPool, MapperOpts.DEFAULT);
    }

    public LazyFieldMapper(Field in, Field out, TypeConverterPool typeConverterPool, MapperOpts mapperOpts) {
        this.inputField = in;
        this.outputField = out;
        this.typeConverterPool = typeConverterPool;
        this.mapperOpts = mapperOpts;
    }

    @Override
    public void map(Object inputObject, Object outputObject) throws NoTypeConverterFoundException {
        assert inputObject != null;
        assert outputObject != null;
        inputField.setAccessible(true);
        outputField.setAccessible(true);
        try {
            Object inputValue = this.inputField.get(inputObject);

            if (inputValue == null && mapperOpts.ignoreNullInput) {
                return;
            }

            if (inputField.getType().equals(outputField.getType())) {
                // require same type
                outputField.set(outputObject, inputValue);
                return;
            }

            TypeConverter typeConverter = typeConverterPool.get(inputField.getType().getName(), outputField.getType().getName());

            if (typeConverter == null) {
                typeConverter = new LazyObjectConverter(inputField.getType(), outputField.getType(), typeConverterPool);
            }

            Object outputValue = typeConverter.convert(inputValue);
            outputField.set(outputObject, outputValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoZeroArgumentsConstructorFoundException e) {
            e.printStackTrace();
        }
    }

}
