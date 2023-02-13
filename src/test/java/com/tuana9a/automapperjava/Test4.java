package com.tuana9a.automapperjava;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
public class Test4 {
    private ObjectId id;
    private Test1 test1;
}
