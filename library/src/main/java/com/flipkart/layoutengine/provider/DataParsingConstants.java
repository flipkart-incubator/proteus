package com.flipkart.layoutengine.provider;

import java.util.regex.Pattern;

/**
 * Contains data binding constants
 */
public class DataParsingConstants {
    public static final Character DATA_PREFIX = '$';
    public static final Character REGEX_PREFIX = '~';
    public static final Pattern REGEX_PATTERN = Pattern.compile("\\{\\{(\\S+?)\\}\\}\\$\\((.+?)\\)|\\{\\{(\\S+?)\\}\\}");
}
