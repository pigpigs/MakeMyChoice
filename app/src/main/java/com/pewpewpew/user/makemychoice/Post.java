package com.pewpewpew.user.makemychoice;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by User on 15/10/14.
 */

@ParseClassName("Post")
public class Post extends ParseObject{
    private String POST_TITLE = "title";
    private String POST_BODY = "mainBody";
    private String POST_POINTS = "points";
    private String POST_IMAGE = "image";

    // Constructor must be empty to avoid ruining anything
    public Post(){

    }

    public String getTitle(){
        return getString(POST_TITLE);
    }

    /**
     * @return String of main post's body
     */
    public String getBody(){
       return getString(POST_BODY);
    }

    public int getPoints(){
        return getInt(POST_POINTS);
    }

    public void setTitle(String title){
        put(POST_TITLE, title);
    }
    public void setBody(String body){
        put(POST_BODY, body);
    }

    public void setPoints(int points){
        put(POST_POINTS, points);
    }

    public void setImage(byte[] data){
        ParseFile file = new ParseFile("post_image.jpg",data);
        put(POST_IMAGE, file);
    }
    public ParseFile getImage(){
        return getParseFile(POST_IMAGE);
    }
}
