package com.tuana9a.automapperjava.mappers;

import com.tuana9a.automapperjava.exceptions.MissingTypeException;
import com.tuana9a.automapperjava.exceptions.NoTypeConverterFoundException;
import com.tuana9a.automapperjava.exceptions.NoZeroArgumentsConstructorFoundException;
import com.tuana9a.automapperjava.interfaces.FieldMapper;
import com.tuana9a.automapperjava.interfaces.TypeConverter;
import com.tuana9a.automapperjava.pools.TypeConverterPool;

import java.lang.reflect.Field;

public class LazyFieldMapper implements FieldMapper {
    private final Field inputField;
    private final Field outputField;
    private final TypeConverterPool typeConverterPool;

    public LazyFieldMapper(Field in, Field out, TypeConverterPool typeConverterPool) {
        this.inputField = in;
        this.outputField = out;
        this.typeConverterPool = typeConverterPool;
    }

    @Override
    public void map(Object inputObject, Object outputObject) throws NoTypeConverterFoundException, MissingTypeException {
        assert inputObject != null;
        assert outputObject != null;
        inputField.setAccessible(true);
        outputField.setAccessible(true);
        try {
            Object inputValue = this.inputField.get(inputObject);

            if (inputValue == null) {
                return;
            }

            if (inputField.getType().equals(outputField.getType())) {
                // require same type
                outputField.set(outputObject, inputValue);
                return;
            }

            TypeConverter typeConverter = typeConverterPool.get(inputField.getType(), outputField.getType());

            if (typeConverter == null) {
                typeConverter = new LazyObjectConverter(inputField.getType(), outputField.getType(), typeConverterPool);
            }

            Object outputValue = typeConverter.convert(inputValue);
            outputField.set(outputObject, outputValue);
        } catch (IllegalAccessException | NoZeroArgumentsConstructorFoundException e) {
            e.printStackTrace();
        }
    }

}
