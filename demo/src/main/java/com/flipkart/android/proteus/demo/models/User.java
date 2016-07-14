package com.flipkart.android.proteus.demo.models;

/**
 * User
 *
 * @author aditya.sharat
 */
public class User {
    public String name;
    public int level;
    public int achievements;
    public int experience;
    public Location location;
    public int credits;

    public static class Location {
        public String country;
        public String city;
        public String pincode;
    }
}
