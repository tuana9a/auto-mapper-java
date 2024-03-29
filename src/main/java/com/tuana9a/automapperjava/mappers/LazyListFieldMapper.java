package com.tuana9a.automapperjava.mappers;

import com.tuana9a.automapperjava.db.TypeConverterDb;
import com.tuana9a.automapperjava.db.TypeMapperDb;
import com.tuana9a.automapperjava.db.ZeroArgsConstructorDb;
import com.tuana9a.automapperjava.exceptions.MissingTypeException;
import com.tuana9a.automapperjava.exceptions.NoTypeMapperFoundException;
import com.tuana9a.automapperjava.exceptions.ZeroArgumentsConstructorNotFoundException;
import com.tuana9a.automapperjava.interfaces.TypeConverter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class LazyListFieldMapper extends LazyFieldMapper {
    public LazyListFieldMapper() {
        super(TypeMapperDb.getInstance(), TypeConverterDb.getInstance(), ZeroArgsConstructorDb.getInstance());
    }

    public LazyListFieldMapper(TypeMapperDb typeMapperDb, TypeConverterDb typeConverterDb, ZeroArgsConstructorDb zeroArgsConstructorDb) {
        super(typeMapperDb, typeConverterDb, zeroArgsConstructorDb);
    }

    @Override
    public void map(Object inputObject, Object outputObject) throws NoTypeMapperFoundException, MissingTypeException {
        assert inputObject != null;
        assert outputObject != null;
        inputField.setAccessible(true);
        outputField.setAccessible(true);

        try {
            List inputValue = (List) this.inputField.get(inputObject);
            List outputValue = new LinkedList<>();

            if (inputValue == null) {
                if (opts.allowNull) {
                    outputField.set(outputObject, null);
                }
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

            TypeConverter converter = this.typeConverterDb.get((Class) inputType, (Class) outputType);

            if (converter == null) {
                converter = new LazyObjectAutoMapper(typeMapperDb, typeConverterDb, zeroArgsConstructorDb).opts(opts).parse((Class) inputType, (Class) outputType);
            }

            for (Object x : inputValue) {
                outputValue.add(converter.convert(x));
            }

            outputField.set(outputObject, outputValue);
        } catch (IndexOutOfBoundsException | IllegalAccessException | ZeroArgumentsConstructorNotFoundException e) {
            e.printStackTrace();
        }
    }

}
