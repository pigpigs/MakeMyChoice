package com.pewpewpew.user.makemychoice;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

/**
 * Created by User on 12/1/15.
 */
@ParseClassName("Outcome")
public class Outcome extends ParseObject{
    private String OUTCOME_BODY = "body";
    private String OUTCOME_IMAGE = "image";
    private String OUTCOME_TITLE = "title";

    public Outcome(){}

    public void setBody(String body){put(OUTCOME_BODY, body);}

    public void setImage(byte[] data){
        ParseFile file = new ParseFile("outcome_image.jpg",data);
        put(OUTCOME_IMAGE, file);
    }
    public void setTitle(String title){
        put(OUTCOME_TITLE, title);
    }

    public String getTitle(){
        return getString(OUTCOME_TITLE);
    }
    public ParseFile getImage(){return getParseFile(OUTCOME_IMAGE);}

    public String getBody(){return getString(OUTCOME_BODY);}
}
