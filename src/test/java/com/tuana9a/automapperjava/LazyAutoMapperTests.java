package com.tuana9a.automapperjava;

import com.tuana9a.automapperjava.common.AutoMapperConstants;
import com.tuana9a.automapperjava.db.TypeConverterDb;
import com.tuana9a.automapperjava.exceptions.AutoMapperException;
import com.tuana9a.automapperjava.utils.LazyAutoMapperUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

public class LazyAutoMapperTests {
    @Test
    public void testMapper() throws AutoMapperException {
        TypeConverterDb typeConverterDb = TypeConverterDb.getInstance();
        typeConverterDb.update(AutoMapperConstants.ASTERISK, String.class.getName(), x -> x == null ? null : String.valueOf(x));
        typeConverterDb.update(String.class, ObjectId.class, x -> Utils.toObjectId(x));
        typeConverterDb.update(String.class, Integer.class, x -> x == null ? null : Integer.parseInt(x));
        typeConverterDb.update(String.class, Long.class, x -> x == null ? null : Long.parseLong(x));
        typeConverterDb.update(String.class, Double.class, x -> x == null ? null : Double.parseDouble(x));

        LazyAutoMapperUtils autoMapperUtils = LazyAutoMapperUtils.getInstance();

        Samples.Person person = new Samples.Person();
        person.setAge(10);
        person.setName("10");

        Samples.Person1 person1 = new Samples.Person1();
        autoMapperUtils.map(person, person1);
        Assertions.assertEquals(person1.getName(), "10");
        Assertions.assertEquals(person1.getAge(), 10);

        Samples.Person2 person2 = new Samples.Person2();
        autoMapperUtils.map(person, person2);
        Assertions.assertEquals(person2.getAge(), "10");
        Assertions.assertEquals(person2.getName(), 10);

        ObjectId objectId = new ObjectId();
        Samples.Person3 person3 = new Samples.Person3();
        person3.setId(objectId);
        person3.setPerson(person);

        Samples.Person4 person4 = new Samples.Person4();
        autoMapperUtils.map(person3, person4);
        Assertions.assertEquals(person4.getId(), objectId.toString());
        Assertions.assertEquals(person4.getPerson().getAge(), "10");
        Assertions.assertEquals(person4.getPerson().getName(), 10);

        Samples.Person5 person5 = new Samples.Person5();
        person5.setPerson(person3);

        Samples.Person6 person6 = new Samples.Person6();
        autoMapperUtils.map(person5, person6);
        Assertions.assertEquals(person6.getPerson().getId(), objectId.toString());
        Assertions.assertEquals(person6.getPerson().getPerson().getAge(), "10");
        Assertions.assertEquals(person6.getPerson().getPerson().getName(), 10);

        List<Samples.Person5> person5List = new LinkedList<>();
        person5List.add(person5);
        List<Samples.Person6> person6List = autoMapperUtils.convertList(person5List, Samples.Person6.class);
        Assertions.assertEquals(person6List.size(), 1);
        Assertions.assertEquals(person6List.get(0).getPerson().getId(), objectId.toString());

        Samples.Family family = new Samples.Family();
        family.setList(person5List);

        Samples.Family1 family1 = new Samples.Family1();
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
        TypeConverterDb typeConverterDb = TypeConverterDb.getInstance();
        typeConverterDb.update(AutoMapperConstants.ASTERISK, String.class.getName(), x -> x == null ? null : String.valueOf(x));
        typeConverterDb.update(String.class, ObjectId.class, x -> Utils.toObjectId(x));
        typeConverterDb.update(String.class, Integer.class, x -> x == null ? null : Integer.parseInt(x));
        typeConverterDb.update(String.class, Long.class, x -> x == null ? null : Long.parseLong(x));
        typeConverterDb.update(String.class, Double.class, x -> x == null ? null : Double.parseDouble(x));

        LazyAutoMapperUtils autoMapperUtils = LazyAutoMapperUtils.getInstance();

        Samples.Person person = new Samples.Person();
        person.setAge(10);
        person.setName("10");

        Samples.Person1 person1 = autoMapperUtils.convert(person, Samples.Person1.class);
        Assertions.assertEquals(person1.getName(), "10");
        Assertions.assertEquals(person1.getAge(), 10);

        Samples.Person2 person2 = autoMapperUtils.convert(person, Samples.Person2.class);
        Assertions.assertEquals(person2.getAge(), "10");
        Assertions.assertEquals(person2.getName(), 10);

        ObjectId objectId = new ObjectId();
        Samples.Person3 person3 = new Samples.Person3();
        person3.setId(objectId);
        person3.setPerson(person);

        Samples.Person4 person4 = autoMapperUtils.convert(person3, Samples.Person4.class);
        Assertions.assertEquals(person4.getId(), objectId.toString());
        Assertions.assertEquals(person4.getPerson().getAge(), "10");
        Assertions.assertEquals(person4.getPerson().getName(), 10);

        Samples.Person5 person5 = new Samples.Person5();
        person5.setPerson(person3);

        Samples.Person6 person6 = autoMapperUtils.convert(person5, Samples.Person6.class);
        Assertions.assertEquals(person6.getPerson().getId(), objectId.toString());
        Assertions.assertEquals(person6.getPerson().getPerson().getAge(), "10");
        Assertions.assertEquals(person6.getPerson().getPerson().getName(), 10);

        List<Samples.Person5> person5List = new LinkedList<>();
        person5List.add(person5);
        List<Samples.Person6> person6List = autoMapperUtils.convertList(person5List, Samples.Person6.class);
        Assertions.assertEquals(person6List.size(), 1);
        Assertions.assertEquals(person6List.get(0).getPerson().getId(), objectId.toString());

        Samples.Family family = new Samples.Family();
        family.setList(person5List);

        Samples.Family1 family1 = autoMapperUtils.convert(family, Samples.Family1.class);
        Assertions.assertNotNull(family1.getList());
        Assertions.assertEquals(family1.getList().size(), 1);
        Assertions.assertEquals(family1.getList()
                .get(0)
                .getPerson()
                .getPerson()
                .getName(), 10);
    }
}
