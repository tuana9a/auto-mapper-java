package com.tuana9a.automapperjava;

import com.tuana9a.automapperjava.configs.AutoMapperConstants;
import com.tuana9a.automapperjava.configs.AutoMapperOpts;
import com.tuana9a.automapperjava.db.TypeConverterDb;
import com.tuana9a.automapperjava.exceptions.AutoMapperException;
import com.tuana9a.automapperjava.utils.LazyAutoMapperUtils;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

public class LazyAutoMapperTests {

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

    @Test
    public void testMapper() throws AutoMapperException {
        LazyAutoMapperUtils autoMapperUtils = LazyAutoMapperUtils.getInstance();
        TypeConverterDb typeConverterDb = autoMapperUtils.getTypeConverterDb();
        typeConverterDb.update(AutoMapperConstants.ASTERISK, String.class.getName(), x -> x == null ? null : String.valueOf(x));
        typeConverterDb.update(String.class, ObjectId.class, x -> Utils.toObjectId(x));
        typeConverterDb.update(String.class, Integer.class, x -> x == null ? null : Integer.parseInt(x));
        typeConverterDb.update(String.class, Long.class, x -> x == null ? null : Long.parseLong(x));
        typeConverterDb.update(String.class, Double.class, x -> x == null ? null : Double.parseDouble(x));

        Person person = new Person();
        person.setAge(10);
        person.setName("10");

        Person1 person1 = new Person1();
        autoMapperUtils.map(person, person1);
        Assertions.assertEquals(person1.getName(), "10");
        Assertions.assertEquals(person1.getAge(), 10);

        Person2 person2 = new Person2();
        autoMapperUtils.map(person, person2);
        Assertions.assertEquals(person2.getAge(), "10");
        Assertions.assertEquals(person2.getName(), 10);

        ObjectId objectId = new ObjectId();
        Person3 person3 = new Person3();
        person3.setId(objectId);
        person3.setPerson(person);

        Person4 person4 = new Person4();
        autoMapperUtils.map(person3, person4);
        Assertions.assertEquals(person4.getId(), objectId.toString());
        Assertions.assertEquals(person4.getPerson().getAge(), "10");
        Assertions.assertEquals(person4.getPerson().getName(), 10);

        Person5 person5 = new Person5();
        person5.setPerson(person3);

        Person6 person6 = new Person6();
        autoMapperUtils.map(person5, person6);
        Assertions.assertEquals(person6.getPerson().getId(), objectId.toString());
        Assertions.assertEquals(person6.getPerson().getPerson().getAge(), "10");
        Assertions.assertEquals(person6.getPerson().getPerson().getName(), 10);

        List<Person5> person5List = new LinkedList<>();
        person5List.add(person5);
        List<Person6> person6List = autoMapperUtils.convertList(person5List, Person6.class);
        Assertions.assertEquals(person6List.size(), 1);
        Assertions.assertEquals(person6List.get(0).getPerson().getId(), objectId.toString());

        Family family = new Family();
        family.setList(person5List);

        Family1 family1 = new Family1();
        autoMapperUtils.map(family, family1);
        Assertions.assertNotNull(family1.getList());
        Assertions.assertEquals(family1.getList().size(), 1);
        Assertions.assertEquals(family1.getList()
                .get(0)
                .getPerson()
                .getPerson()
                .getName(), 10);
    }

    @Test
    public void testConverter() throws AutoMapperException {
        LazyAutoMapperUtils autoMapperUtils = LazyAutoMapperUtils.getInstance();
        TypeConverterDb typeConverterDb = autoMapperUtils.getTypeConverterDb();
        typeConverterDb.update(AutoMapperConstants.ASTERISK, String.class.getName(), x -> x == null ? null : String.valueOf(x));
        typeConverterDb.update(String.class, ObjectId.class, x -> Utils.toObjectId(x));
        typeConverterDb.update(String.class, Integer.class, x -> x == null ? null : Integer.parseInt(x));
        typeConverterDb.update(String.class, Long.class, x -> x == null ? null : Long.parseLong(x));
        typeConverterDb.update(String.class, Double.class, x -> x == null ? null : Double.parseDouble(x));

        Person person = new Person();
        person.setAge(10);
        person.setName("10");

        Person1 person1 = autoMapperUtils.convert(person, Person1.class);
        Assertions.assertEquals(person1.getName(), "10");
        Assertions.assertEquals(person1.getAge(), 10);

        Person2 person2 = autoMapperUtils.convert(person, Person2.class);
        Assertions.assertEquals(person2.getAge(), "10");
        Assertions.assertEquals(person2.getName(), 10);

        ObjectId objectId = new ObjectId();
        Person3 person3 = new Person3();
        person3.setId(objectId);
        person3.setPerson(person);

        Person4 person4 = autoMapperUtils.convert(person3, Person4.class);
        Assertions.assertEquals(person4.getId(), objectId.toString());
        Assertions.assertEquals(person4.getPerson().getAge(), "10");
        Assertions.assertEquals(person4.getPerson().getName(), 10);

        Person5 person5 = new Person5();
        person5.setPerson(person3);

        Person6 person6 = autoMapperUtils.convert(person5, Person6.class);
        Assertions.assertEquals(person6.getPerson().getId(), objectId.toString());
        Assertions.assertEquals(person6.getPerson().getPerson().getAge(), "10");
        Assertions.assertEquals(person6.getPerson().getPerson().getName(), 10);

        List<Person5> person5List = new LinkedList<>();
        person5List.add(person5);
        List<Person6> person6List = autoMapperUtils.convertList(person5List, Person6.class);
        Assertions.assertEquals(person6List.size(), 1);
        Assertions.assertEquals(person6List.get(0).getPerson().getId(), objectId.toString());

        Family family = new Family();
        family.setList(person5List);

        Family1 family1 = autoMapperUtils.convert(family, Family1.class);
        Assertions.assertNotNull(family1.getList());
        Assertions.assertEquals(family1.getList().size(), 1);
        Assertions.assertEquals(family1.getList()
                .get(0)
                .getPerson()
                .getPerson()
                .getName(), 10);
    }

    @Test
    public void testAllowNull() throws AutoMapperException {
        LazyAutoMapperUtils autoMapperUtils = LazyAutoMapperUtils.getInstance();
        autoMapperUtils.initDefaultMapper();
        AutoMapperOpts.DEFAULT.allowNull = true;

        Person1 p1 = new Person1();
        p1.setName(null);
        p1.setAge(null);
        Person2 p2 = new Person2();
        p2.setName(1234);
        p2.setAge("12");
        autoMapperUtils.map(p1, p2);
        Assertions.assertNull(p2.getName());
        Assertions.assertNull(p2.getAge());

        Family f1 = new Family();
        f1.setList(null);
        Family1 f2 = new Family1();
        f2.setList(new LinkedList<>());
        autoMapperUtils.map(f1, f2);
        Assertions.assertNull(f2.getList());
    }

    @Test
    public void testAllowNullByPassingOpts() throws AutoMapperException {
        LazyAutoMapperUtils autoMapperUtils = LazyAutoMapperUtils.getInstance();
        autoMapperUtils.initDefaultMapper();
        AutoMapperOpts opts = new AutoMapperOpts();
        opts.allowNull = true;
        autoMapperUtils.opts(opts);

        Person1 p1 = new Person1();
        p1.setName(null);
        p1.setAge(null);
        Person2 p2 = new Person2();
        p2.setName(1234);
        p2.setAge("12");
        autoMapperUtils.map(p1, p2);
        Assertions.assertNull(p2.getName());
        Assertions.assertNull(p2.getAge());

        Family f1 = new Family();
        f1.setList(null);
        Family1 f2 = new Family1();
        f2.setList(new LinkedList<>());
        autoMapperUtils.map(f1, f2);
        Assertions.assertNull(f2.getList());
    }
}
