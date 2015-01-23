package com.pewpewpew.user.makemychoice;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by User on 15/10/14.
 */

@ParseClassName("Post")
public class Post extends ParseObject{

    private static final String POST_OUTCOME = "outcome";
    public static final String POST_NUMCOMMENTS = "numComments";
    private String POST_TITLE = "title";
    private String POST_BODY = "mainBody";
    private String POST_POINTS = "points";
    private String POST_IMAGE = "image";
    private String POST_USER = "user";
    private static final String POST_USER_STR = "usernameStr";

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

    public Outcome getOutcome(){return (Outcome) getParseObject(POST_OUTCOME);}

    public ParseUser getUser(){
        return getParseUser(POST_USER);
    }

    public String getUserStr(){return getString(POST_USER_STR);}
    public void setImage(byte[] data){
        ParseFile file = new ParseFile("post_image.jpg",data);
        put(POST_IMAGE, file);
    }
    public ParseFile getImage(){
        return getParseFile(POST_IMAGE);
    }

    public int getNumComments(){return getInt(POST_NUMCOMMENTS);}

    public void setTitle(String title){
        put(POST_TITLE, title);
    }
    public void setBody(String body){
        put(POST_BODY, body);
    }

    public void setPoints(int points){
        put(POST_POINTS, points);
    }

    public void setOutcome(Outcome outcome){put(POST_OUTCOME, outcome);}

    public void setUser(ParseUser user){put(POST_USER, user);}



    public void setCurrentUser(){
        ParseUser user = ParseUser.getCurrentUser();
        put(POST_USER, user);

        // ADDED TO SPEED UP VIEW INFLATION, LOADING OBJECTS TAKE TOO LONG
        put(POST_USER_STR, user.getUsername());
    }



    public void incrementNumComments(){
        increment(POST_NUMCOMMENTS);
    }

}
