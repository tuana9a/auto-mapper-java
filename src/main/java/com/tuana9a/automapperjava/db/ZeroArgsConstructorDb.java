package com.tuana9a.automapperjava.db;

import com.tuana9a.automapperjava.exceptions.ZeroArgumentsConstructorNotFoundException;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ZeroArgsConstructorDb {
    private final Map<String, Constructor> db;
    private static final ZeroArgsConstructorDb INSTANCE = new ZeroArgsConstructorDb();

    public static ZeroArgsConstructorDb getInstance() {
        return INSTANCE;
    }

    public ZeroArgsConstructorDb() {
        this.db = new HashMap<>();
    }

    public <O> void update(Class<O> clazz, Constructor<O> constructor) {
        this.db.put(clazz.getName(), constructor);
    }

    public <O> void updateIfNotExist(Class<O> clazz) throws ZeroArgumentsConstructorNotFoundException {
        if (!ifExist(clazz)) {
            Constructor zeroArgsConstructor = Arrays.stream(clazz.getConstructors()).filter(x -> x.getParameters().length == 0).findFirst().orElseThrow(() -> new ZeroArgumentsConstructorNotFoundException(clazz));
            this.update(clazz, zeroArgsConstructor);
        }
    }

    public <O> Constructor<O> get(Class<O> clazz) {
        return this.db.get(clazz.getName());
    }

    public <O> Constructor<O> get(String key) {
        return this.db.get(key);
    }

    public boolean ifExist(Class clazz) {
        return this.db.get(clazz.getName()) != null;
    }
}
