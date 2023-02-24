package com.tuana9a.automapperjava;

import org.bson.types.ObjectId;

public class Utils {
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
