package com.flipkart.layoutengine.demo.models;

import java.util.List;

/**
 * User
 *
 * @author aditya.sharat
 */
public class User {
    /*
{
  "user": {
    "name": "John Doe",
    "level": 4,
    "achievements": 16,
    "experience": 2791,
    "location": "India",
    "credits": "39550"
  },
  "data": {
    "totalAchievements": 114,
    "tags": ["Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Mike"]
  }
}
    */

    public String name;
    public int level;
    public int achievements;
    public int experience;
    public String location;
    public int credits;
    public Data data;

    public static class Data {
        public int totalAchievements;
        public List<String> tags;
    }
}
