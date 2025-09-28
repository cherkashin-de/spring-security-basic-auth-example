package com.security.auth.service.utils;

import java.util.Collection;

public class FieldsHelper {

    public static boolean isFilled(Collection<?> list) {
        return list != null && !list.isEmpty();
    }

    public static boolean isFilled(String value) {
        return value != null && !value.isEmpty();
    }

}
