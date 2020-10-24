package org.jeuxdemots.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JDMUtil {
    private JDMUtil() {
    }

    private static final Pattern KEY_VALUE = Pattern.compile("([^=]*)=([^=]*)");
    private static final Pattern KEY_VALUE_QUOTED_PATTERN = Pattern.compile("([^=]*)=\"([^\"]*)\"$");

    public static Map<String, String> parseDataLine(final String line, final String separatorChar) {
        final Map<String, String> lineData = new HashMap<>();
        final String[] fields = line.split(separatorChar);
        for (final String field : fields) {
            Matcher keyValueMatcher = KEY_VALUE_QUOTED_PATTERN.matcher(field);
            if (!keyValueMatcher.matches()) {
                keyValueMatcher = KEY_VALUE.matcher(field);
            }
            if (keyValueMatcher.matches()) {
                lineData.put(keyValueMatcher.group(1), keyValueMatcher.group(2));
            }
        }
        return lineData;
    }
}
