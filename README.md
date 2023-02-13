# auto-mapper-java

```java
import com.tuana9a.automapperjava.interfaces.AutoMapper;
import com.tuana9a.automapperjava.utils.LazyAutoMapperUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class Main {
    @Getter
    @Setter
    @AllArgsConstructor
    private static class Person1 {
        private String name;
        private Integer age;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class Person2 {
        private String name;
        private Integer age;
    }

    public static void main(String[] args) {
        AutoMapper autoMapper = new LazyAutoMapperUtils();
        Person1 p1 = new Person1("alice", 23);
        Person2 p2 = autoMapper.convert(p1, Person2.class);
        System.out.println(p2.getName()); // alice
        System.out.println(p2.getAge()); // 23
    }
}
```