package org.example.core.common.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtil {

    public static String firstLetterToLowerCase(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    public static String firstLetterToUpperCase(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static boolean isRegex(String str) {
        boolean isRegex = true;
        if(!StringUtils.hasText(str))
            return false;
        try {
         Pattern.compile(str);
        }catch (PatternSyntaxException exception) {
            isRegex = false;
        }

        return isRegex;
    }
}
