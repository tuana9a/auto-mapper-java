package com.tuana9a.automapperjava.mappers;

import com.tuana9a.automapperjava.auto.LazyObjectAutoMapper;
import com.tuana9a.automapperjava.db.TypeConverterDb;
import com.tuana9a.automapperjava.db.ZeroArgsConstructorDb;
import com.tuana9a.automapperjava.exceptions.MissingTypeException;
import com.tuana9a.automapperjava.exceptions.NoTypeMapperFoundException;
import com.tuana9a.automapperjava.exceptions.ZeroArgumentsConstructorNotFoundException;
import com.tuana9a.automapperjava.interfaces.FieldMapper;
import com.tuana9a.automapperjava.interfaces.TypeConverter;

import java.lang.reflect.Field;

public class LazyFieldMapper implements FieldMapper {
    private final Field inputField;
    private final Field outputField;

    public LazyFieldMapper(Field in, Field out) {
        this.inputField = in;
        this.outputField = out;
    }

    @Override
    public void map(Object inputObject, Object outputObject) throws NoTypeMapperFoundException, MissingTypeException {
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

            TypeConverter converter = TypeConverterDb.getInstance().get(inputField.getType(), outputField.getType());

            if (converter == null) {
                converter = new LazyObjectAutoMapper(inputField.getType(), outputField.getType());
            }

            Object outputValue = converter.convert(inputValue);
            outputField.set(outputObject, outputValue);
        } catch (IllegalAccessException | ZeroArgumentsConstructorNotFoundException e) {
            e.printStackTrace();
        }
    }

}
