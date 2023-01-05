package com.techproai.automapperjava.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(RepeatableAutoMapperFrom.class)
public @interface AutoMapperFrom {
    Class<?> value();
}
