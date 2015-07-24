package com.flipkart.layoutengine.provider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Contains data binding constants
 */
public class ProteusConstants {
    public static final String TYPE = "type";
    public static final String CHILDREN = "children";
    public static final String CHILD_TYPE = "childType";
    public static final String STYLE = "style";
    public static final String STYLE_DELIMITER = "\\.";
    public static final String DATA_CONTEXT = "dataContext";
    public static final String CHILD_DATA_CONTEXT = "childDataContext";
    public static final String DATA_VISIBILITY = "data";
    public static final Character DATA_PREFIX = '$';
    public static final Character REGEX_PREFIX = '~';
    public static final Pattern REGEX_PATTERN = Pattern.compile("\\{\\{(\\S+?)\\}\\}\\$\\((.+?)\\)|\\{\\{(\\S+?)\\}\\}");
    public static final String DATA_PATH_DELIMITER = "\\.|\\[|\\]";
    public static final String DATA_PATH_SIMPLE_DELIMITER = "\\.";
    public static final String CHILD_INDEX_REFERENCE = "$index";
    public static final String ARRAY_DATA_LENGTH_REFERENCE = "$length";
    public static final String ARRAY_DATA_LAST_INDEX_REFERENCE = "$last";

    static final String[] attributeToIgnore = new String[]{"android", "dataContext"};
    public static final Set<String> ATTRIBUTES_TO_IGNORE = new HashSet<>(Arrays.asList(attributeToIgnore));
}
