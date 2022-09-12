package com.iagent.util;

/**
 * <b>String common type handler</b>
 */
public class StringUtils {

    /**
     * empty string
     */
    public static final String EMPTY_STRING = "";

    /**
     * <b>get string between startWiths and endWiths by content
     *    (abc(ef)) --> abc(ef 
     *  get first tag
     * </b>
     * @param content
     * @param startWiths
     * @param endWiths
     * @return
     */
    public static String getStringByTags(String content, String startWiths, String endWiths){
        if(content.contains(startWiths) && content.contains(endWiths)){
            int startIndex = content.indexOf(startWiths) + startWiths.length();
            int endIndex = content.indexOf(endWiths);
            return content.substring(startIndex,endIndex);
        }
        return null;
    }

    /**
     * check object null
     * @param object
     * @return
     */
    public static boolean isEmpty(String object) {
        return object == null || "".equals(object) || "null".equals(object.toLowerCase()) || object.isEmpty();
    }

    public static boolean isNotEmpty(String object) {
        return !isEmpty(object);
    }

    /**
     * Replace all occurrences of a substring within a string with another string.
     * @param inString
     * @param oldPattern
     * @param newPattern
     * @return
     */
    public static String replace(String inString, String oldPattern, String newPattern) {
        if (!isNotEmpty(inString) || !isNotEmpty(oldPattern) || newPattern == null) {
            return inString;
        }
        int index = inString.indexOf(oldPattern);
        if (index == -1) {
            // no occurrence -> can return input as-is
            return inString;
        }

        int capacity = inString.length();
        if (newPattern.length() > oldPattern.length()) {
            capacity += 16;
        }
        StringBuilder sb = new StringBuilder(capacity);

        int pos = 0;  // our position in the old string
        int patLen = oldPattern.length();
        while (index >= 0) {
            sb.append(inString, pos, index);
            sb.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }

        // append any characters to the right of a match
        sb.append(inString, pos, inString.length());
        return sb.toString();
    }

    /**
     * Deletes consecutive specified characters
     * @param inString
     * @param deleteChar
     * @param isStart
     * @return
     */
    public static String deleteSpecifiedChar(String inString, char deleteChar, boolean isStart) {
        if (StringUtils.isEmpty(inString)) {
            return StringUtils.EMPTY_STRING;
        }
        char[] chars = inString.toCharArray();
        if (isStart) {
            if (!inString.startsWith(String.valueOf(deleteChar))) {
                return inString;
            }
            int index = 0;
            for (int i = 0; i < chars.length; i ++) {
                if (chars[i] == deleteChar) {
                    index = i;
                } else {
                    break;
                }
            }
            return inString.substring(index + 1);
        } else {
            if (!inString.endsWith(String.valueOf(deleteChar))) {
                return inString;
            }
            int index = chars.length;
            for (int i = chars.length - 1; i > 0; i --) {
                if (chars[i] == deleteChar) {
                    index = i;
                } else {
                    break;
                }
            }
            return inString.substring(0, index);
        }
    }

    /**
     * 判断两个字符串是否相等
     * @param source
     * @param target
     * @return
     */
    public static boolean isEquals(String source, String target) {
        if (source == target) {
            return true;
        }
        if (source != null && source.equals(target)) {
            return true;
        }
        if (target != null && target.equals(source)) {
            return true;
        }
        return false;
    }
    
    /**
     * <b>get string between startWiths and endWiths by content
     *    (abc(ef)) --> abc(ef)
     *  get the first start tag and end last tag
     * </b>
     * @param content
     * @param startTag
     * @param lastEndTag
     * @return
     */
    public static String getStringLastByTags(String content, String startTag, String lastEndTag) {
        if(content.contains(startTag) && content.contains(lastEndTag)){
            int startIndex = content.indexOf(startTag) + startTag.length();
            int endIndex = content.lastIndexOf(lastEndTag);
            return content.substring(startIndex,endIndex);
        }
        
        return null;
    }
}
