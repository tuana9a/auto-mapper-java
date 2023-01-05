package com.techproai.automapperjava;

import com.techproai.automapperjava.converters.SimpleObjectConverter;
import com.techproai.automapperjava.exceptions.AutoMapperException;
import com.techproai.automapperjava.interfaces.TypeConverter;
import com.techproai.automapperjava.pools.TypeConverterPool;
import com.techproai.automapperjava.utils.AutoMapperUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class AutoMapperTests {
    @Test
    public void test1() throws AutoMapperException {
        TypeConverterPool typeConverterPool = new TypeConverterPool();

        typeConverterPool.add(ObjectId.class, String.class, x -> x == null ? null : String.valueOf(x));
        typeConverterPool.add(Integer.class, String.class, x -> x == null ? null : String.valueOf(x));
        typeConverterPool.add(Long.class, String.class, x -> x == null ? null : String.valueOf(x));
        typeConverterPool.add(Double.class, String.class, x -> x == null ? null : String.valueOf(x));

        typeConverterPool.add(String.class, ObjectId.class, x -> ObjectIdUtils.toObjectId(x));
        typeConverterPool.add(String.class, Integer.class, x -> x == null ? null : Integer.parseInt(x));
        typeConverterPool.add(String.class, Long.class, x -> x == null ? null : Long.parseLong(x));
        typeConverterPool.add(String.class, Double.class, x -> x == null ? null : Double.parseDouble(x));

        TypeConverter<Test1, Test2> soc12 = new SimpleObjectConverter<>(Test1.class, Test2.class, typeConverterPool);
        typeConverterPool.add(Test1.class, Test2.class, x -> soc12.apply(x));

        TypeConverter<Test1, Test3> soc13 = new SimpleObjectConverter<>(Test1.class, Test3.class, typeConverterPool);
        typeConverterPool.add(Test1.class, Test3.class, x -> soc13.apply(x));

        TypeConverter<Test4, Test5> soc45 = new SimpleObjectConverter<>(Test4.class, Test5.class, typeConverterPool);
        typeConverterPool.add(Test4.class, Test5.class, x -> soc45.apply(x));

        TypeConverter<Test6, Test7> soc67 = new SimpleObjectConverter<>(Test6.class, Test7.class, typeConverterPool);
        typeConverterPool.add(Test6.class, Test7.class, x -> soc67.apply(x));

        TypeConverter<Test8, Test9> soc89 = new SimpleObjectConverter<>(Test8.class, Test9.class, typeConverterPool);
        typeConverterPool.add(Test8.class, Test9.class, x -> soc89.apply(x));

        AutoMapperUtils autoMapperUtils = new AutoMapperUtils(typeConverterPool);

        Test1 test1 = new Test1();
        test1.setAge(10);
        test1.setName("10");

        Test2 test2 = autoMapperUtils.convert(test1, Test2.class);
        assert Objects.equals(test2.getName(), "10");
        assert test2.getAge() == 10;

        Test3 test3 = autoMapperUtils.convert(test1, Test3.class);
        assert test3.getAge().equals("10");
        assert test3.getName() == 10;

        ObjectId objectId = new ObjectId();
        Test4 test4 = new Test4();
        test4.setId(objectId);
        test4.setTest1(test1);

        Test5 test5 = autoMapperUtils.convert(test4, Test5.class);
        assert test5.getId().equals(objectId.toString());
        assert test5.getTest1().getAge().equals("10");
        assert test5.getTest1().getName() == 10;

        Test6 test6 = new Test6();
        test6.setTest4(test4);

        Test7 test7 = autoMapperUtils.convert(test6, Test7.class);
        assert test7.getTest4().getId().equals(objectId.toString());
        assert test7.getTest4().getTest1().getAge().equals("10");
        assert test7.getTest4().getTest1().getName() == 10;

        List<Test6> test6s = new LinkedList<>();
        test6s.add(test6);
        List<Test7> test7s = autoMapperUtils.convertList(test6s, Test7.class);
        assert test7s.size() == 1;
        assert test7s.get(0).getTest4().getId().equals(objectId.toString());

        Test8 test8 = new Test8();
        test8.setTest6s(test6s);
        Test9 test9 = autoMapperUtils.convert(test8, Test9.class);
        assert test9.getTest6s() != null;
        assert test9.getTest6s().size() == 1;
        assert test9.getTest6s()
                .get(0)
                .getTest4()
                .getTest1()
                .getName() == 10;
    }
}
