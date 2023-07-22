# auto-mapper-java

## Examples

`Person1.java`

```java
package com.tuana9a.automapperjava.tmp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // need zero args constructor: use lombok or explicit define it
public class Person1 {
    private String name;
    private Integer age;
}
```

`Person2.java`

```java
package com.tuana9a.automapperjava.tmp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Person2 {
    public Person2() { // required
    }

    private String name;
    private Integer age;
}
```

Then how to map them.

```java
package com.tuana9a.automapperjava.tmp;

import com.tuana9a.automapperjava.exceptions.AutoMapperException;
import com.tuana9a.automapperjava.interfaces.AutoMapper;
import com.tuana9a.automapperjava.utils.LazyAutoMapperUtils;

public class README {
    public static void main(String[] args) throws AutoMapperException {
        AutoMapper autoMapper = LazyAutoMapperUtils.getInstance();
        Person1 p1 = new Person1();
        p1.setName("alice");
        p1.setAge(23);
        Person2 p2 = autoMapper.convert(p1, Person2.class);
        System.out.println(p2.getName()); // alice
        System.out.println(p2.getAge()); // 23
    }
}
```