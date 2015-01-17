package com.pewpewpew.user.makemychoice;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by User on 31/12/14.
 */
@ParseClassName("Comments")
public class Comment extends ParseObject {
    private static final String COMMENTS_USER_STR = "usernameStr";
    private String COMMENTS_BODY = "body";
    private String COMMENTS_POINTS = "points";
    private String COMMENTS_USER = "user";
    // According to their documentation using parentPost pointers and doing a reverse search from Post for child comments is faster
    private String COMMENTS_POST = "parentPost";
    public Comment(){

    }

    @Override
    public String toString() {
        return String.format("ParseObject.Comments: %s", getBody() );
    }

    /**
     * @return String of COMMENT's body
     */
    public String getBody(){
        return getString(COMMENTS_BODY);
    }

    public int getPoints(){
        return getInt(COMMENTS_POINTS);
    }
    public Post getPost(Post post){return (Post) getParseObject(COMMENTS_POST);}
    public ParseUser getUser(){
        return getParseUser(COMMENTS_USER);
    }
    public String getUserStr(){return getString(COMMENTS_USER_STR);}
    
    public void setBody(String body){
        put(COMMENTS_BODY, body);
    }
    public void setPost(Post post){put(COMMENTS_POST, post);}
    public void setPoints(int points){put(COMMENTS_POINTS, points);}
    public void setUser(ParseUser user){put(COMMENTS_USER, user);}
    
    
    public void setCurrentUser(){
        ParseUser user = ParseUser.getCurrentUser();
        put(COMMENTS_USER, user);
        put(COMMENTS_USER_STR, user.getUsername());
    }

    


}
