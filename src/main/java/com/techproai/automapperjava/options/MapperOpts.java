package com.techproai.automapperjava.options;

public class MapperOpts {
    public static MapperOpts DEFAULT = new MapperOpts(true);
    public final boolean ignoreNullInput;

    public MapperOpts(boolean ignoreNullInput) {
        this.ignoreNullInput = ignoreNullInput;
    }
}
