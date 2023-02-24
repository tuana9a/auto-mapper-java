package com.tuana9a.automapperjava;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

public class Samples {
    @Getter
    @Setter
    public static class Person {
        private String name;
        private Integer age;
    }

    @Getter
    @Setter
    public static class Person1 {
        private String name;
        private Integer age;
    }

    @Getter
    @Setter
    public static class Person2 {
        private Integer name;
        private String age;
    }

    @Getter
    @Setter
    public static class Person3 {
        private ObjectId id;
        private Person person;
    }

    @Getter
    @Setter
    public static class Person4 {
        private String id;
        private Person2 person;
    }

    @Getter
    @Setter
    public static class Person5 {
        private Person3 person;
    }

    @Getter
    @Setter
    public static class Person6 {
        private Person4 person;
    }

    @Getter
    @Setter
    public static class Family {
        private List<Person5> list;
    }

    @Getter
    @Setter
    public static class Family1 {
        private List<Person6> list;
    }
}
