package com.techproai.automapperjava.mappers;

import com.techproai.automapperjava.exceptions.MissingTypeException;
import com.techproai.automapperjava.exceptions.NoTypeConverterFoundException;
import com.techproai.automapperjava.exceptions.NoZeroArgumentsConstructorFoundException;
import com.techproai.automapperjava.interfaces.FieldMapper;
import com.techproai.automapperjava.interfaces.TypeConverter;
import com.techproai.automapperjava.pools.TypeConverterPool;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class LazyListFieldMapper implements FieldMapper {
    private final Field inputField;
    private final Field outputField;
    private final TypeConverterPool typeConverterPool;

    public LazyListFieldMapper(Field in, Field out, TypeConverterPool typeConverterPool) {
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
            List inputValue = (List) this.inputField.get(inputObject);
            List outputValue = new LinkedList<>();

            if (inputValue == null) {
                return;
            }

            if (inputValue.size() == 0) {
                outputField.set(outputObject, new LinkedList<>());
                return;
            }

            ParameterizedType inputParameterizedType = (ParameterizedType) inputField.getGenericType();
            ParameterizedType outputParameterizedTypeType = (ParameterizedType) outputField.getGenericType();

            Type[] inputActualTypeArguments = inputParameterizedType.getActualTypeArguments();
            Type[] outputActualTypeArguments = outputParameterizedTypeType.getActualTypeArguments();

            if (inputActualTypeArguments.length == 0 && outputActualTypeArguments.length == 0) {
                // no generic found so set output equals to input value
                outputField.set(outputObject, inputValue);
                return;
            }

            if (inputActualTypeArguments.length == 0 || outputActualTypeArguments.length == 0) {
                // one in two missing generic type
                throw new MissingTypeException(String.join(".", inputField.getDeclaringClass().getName(), inputField.getName()),
                        String.join(".", outputField.getDeclaringClass().getName(), outputField.getName()));
            }

            Type inputType = inputActualTypeArguments[0];
            Type outputType = outputActualTypeArguments[0];

            if (inputType.equals(outputType)) {
                // each element is the same type
                outputField.set(outputObject, inputValue);
                return;
            }

            TypeConverter typeConverter = typeConverterPool.get((Class) inputType, (Class) outputType);

            if (typeConverter == null) {
                typeConverter = new LazyObjectConverter((Class) inputType, (Class) outputType, typeConverterPool);
            }

            for (Object x : inputValue) {
                outputValue.add(typeConverter.convert(x));
            }

            outputField.set(outputObject, outputValue);
        } catch (IndexOutOfBoundsException | IllegalAccessException | NoZeroArgumentsConstructorFoundException e) {
            e.printStackTrace();
        }
    }

}
