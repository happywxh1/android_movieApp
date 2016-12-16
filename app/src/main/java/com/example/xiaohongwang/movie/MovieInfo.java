package com.example.xiaohongwang.movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by happywxh on 12/5/16.
 */
public class MovieInfo {
    String movieName;
    String imagePath;

    public MovieInfo(String movieName, String imagePath) {
        //Pass the json file returned from db to find the info
        this.imagePath = imagePath;
        this.movieName = movieName;
    }
}
