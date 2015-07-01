package com.flipkart.layoutengine.exceptions;

/**
 * NoSuchDataPathException
 *
 * @author Aditya Sharat
 */
public class NoSuchDataPathException extends Exception {
    private String message;

    public NoSuchDataPathException(String dataPath) {
        message = dataPath + " not found";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
