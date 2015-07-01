package com.flipkart.layoutengine.exceptions;

/**
 * JsonNullException
 *
 * @author Aditya Sharat
 */
public class JsonNullException extends Exception {
    private String message;

    public JsonNullException(String dataPath) {
        message = dataPath + " is a null object";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
