package com.techproai.automapperjava;

import com.techproai.automapperjava.common.AutoMapperConstants;
import com.techproai.automapperjava.exceptions.AutoMapperException;
import com.techproai.automapperjava.pools.TypeConverterPool;
import com.techproai.automapperjava.utils.LazyAutoMapperUtils;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

public class LazyAutoMapperTests {
    @Test
    public void test1() throws AutoMapperException {
        TypeConverterPool typeConverterPool = new TypeConverterPool();

        typeConverterPool.add(AutoMapperConstants.ASTERISK, String.class.getName(), x -> x == null ? null : String.valueOf(x));
        typeConverterPool.add(String.class, ObjectId.class, x -> ObjectIdUtils.toObjectId(x));
        typeConverterPool.add(String.class, Integer.class, x -> x == null ? null : Integer.parseInt(x));
        typeConverterPool.add(String.class, Long.class, x -> x == null ? null : Long.parseLong(x));
        typeConverterPool.add(String.class, Double.class, x -> x == null ? null : Double.parseDouble(x));

        LazyAutoMapperUtils autoMapperUtils = new LazyAutoMapperUtils(typeConverterPool);

        Test1 test1 = new Test1();
        test1.setAge(10);
        test1.setName("10");

        Test2 test2 = autoMapperUtils.convert(test1, Test2.class);
        Assertions.assertEquals(test2.getName(), "10");
        Assertions.assertEquals(test2.getAge(), 10);

        Test3 test3 = autoMapperUtils.convert(test1, Test3.class);
        Assertions.assertEquals(test3.getAge(), "10");
        Assertions.assertEquals(test3.getName(), 10);

        ObjectId objectId = new ObjectId();
        Test4 test4 = new Test4();
        test4.setId(objectId);
        test4.setTest1(test1);

        Test5 test5 = autoMapperUtils.convert(test4, Test5.class);
        Assertions.assertEquals(test5.getId(), objectId.toString());
        Assertions.assertEquals(test5.getTest1().getAge(), "10");
        Assertions.assertEquals(test5.getTest1().getName(), 10);

        Test6 test6 = new Test6();
        test6.setTest4(test4);

        Test7 test7 = autoMapperUtils.convert(test6, Test7.class);
        Assertions.assertEquals(test7.getTest4().getId(), objectId.toString());
        Assertions.assertEquals(test7.getTest4().getTest1().getAge(), "10");
        Assertions.assertEquals(test7.getTest4().getTest1().getName(), 10);

        List<Test6> test6s = new LinkedList<>();
        test6s.add(test6);
        List<Test7> test7s = autoMapperUtils.convertList(test6s, Test7.class);
        Assertions.assertEquals(test7s.size(), 1);
        Assertions.assertEquals(test7s.get(0).getTest4().getId(), objectId.toString());

        Test8 test8 = new Test8();
        test8.setTest6s(test6s);
        Test9 test9 = autoMapperUtils.convert(test8, Test9.class);
        Assertions.assertNotNull(test9.getTest6s());
        Assertions.assertEquals(test9.getTest6s().size(), 1);
        Assertions.assertEquals(test9.getTest6s()
                .get(0)
                .getTest4()
                .getTest1()
                .getName(), 10);
    }
}
