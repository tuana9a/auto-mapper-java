package com.techproai.automapperjava;

import org.bson.types.ObjectId;

public class ObjectIdUtils {
    public static ObjectId toObjectId(String input) {
        if (input == null || input.matches("^\\s*$") || input.equalsIgnoreCase("null") || input.equalsIgnoreCase("undefined")) {
            return null;
        }
        if (!ObjectId.isValid(input)) {
            return null;
        }
        return new ObjectId(input);
    }
}
