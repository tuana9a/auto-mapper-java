package com.tuana9a.automapperjava.mappers;

import com.tuana9a.automapperjava.configs.AutoMapperOpts;
import com.tuana9a.automapperjava.db.TypeConverterDb;
import com.tuana9a.automapperjava.db.TypeMapperDb;
import com.tuana9a.automapperjava.db.ZeroArgsConstructorDb;
import com.tuana9a.automapperjava.exceptions.MissingTypeException;
import com.tuana9a.automapperjava.exceptions.NoTypeMapperFoundException;
import com.tuana9a.automapperjava.exceptions.ZeroArgumentsConstructorNotFoundException;
import com.tuana9a.automapperjava.interfaces.FieldMapper;
import com.tuana9a.automapperjava.interfaces.TypeConverter;

import java.lang.reflect.Field;

public class LazyFieldMapper implements FieldMapper {
    protected Field inputField;
    protected Field outputField;
    protected final TypeMapperDb typeMapperDb;
    protected final TypeConverterDb typeConverterDb;
    protected final ZeroArgsConstructorDb zeroArgsConstructorDb;
    protected AutoMapperOpts opts;

    public LazyFieldMapper() {
        this(TypeMapperDb.getInstance(), TypeConverterDb.getInstance(), ZeroArgsConstructorDb.getInstance());
    }

    public LazyFieldMapper(TypeMapperDb typeMapperDb, TypeConverterDb typeConverterDb, ZeroArgsConstructorDb zeroArgsConstructorDb) {
        this.typeMapperDb = typeMapperDb;
        this.typeConverterDb = typeConverterDb;
        this.zeroArgsConstructorDb = zeroArgsConstructorDb;
        this.opts = AutoMapperOpts.DEFAULT;
    }

    public LazyFieldMapper in(Field in) {
        this.inputField = in;
        return this;
    }

    public LazyFieldMapper out(Field out) {
        this.outputField = out;
        return this;
    }

    public LazyFieldMapper opts(AutoMapperOpts opts) {
        this.opts = opts;
        return this;
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
                if (opts.allowNull) {
                    outputField.set(outputObject, null);
                }
                return;
            }

            if (inputField.getType().equals(outputField.getType())) {
                // require same type
                outputField.set(outputObject, inputValue);
                return;
            }

            TypeConverter converter = this.typeConverterDb.get(inputField.getType(), outputField.getType());

            if (converter == null) {
                converter = new LazyObjectAutoMapper(typeMapperDb, typeConverterDb, zeroArgsConstructorDb).opts(opts).parse(inputField.getType(), outputField.getType());
            }

            Object outputValue = converter.convert(inputValue);
            outputField.set(outputObject, outputValue);
        } catch (IllegalAccessException | ZeroArgumentsConstructorNotFoundException e) {
            e.printStackTrace();
        }
    }

}
