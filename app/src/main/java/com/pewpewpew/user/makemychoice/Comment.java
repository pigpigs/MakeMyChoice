package com.pewpewpew.user.makemychoice;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by User on 31/12/14.
 */
@ParseClassName("Comments")
public class Comment extends ParseObject {
    private String COMMENTS_BODY = "body";
    private String COMMENTS_POINTS = "points";
    private String COMMENTS_USER = "user";
    // According to their documentation using parentPost pointers and doing a reverse search from Post for child comments is faster
    private String COMMENTS_POST = "parentPost";
    public Comment(){

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

    public void setBody(String body){
        put(COMMENTS_BODY, body);
    }
    public void setPost(Post post){put(COMMENTS_POST, post);}
    public void setPoints(int points){put(COMMENTS_POINTS, points);}


}
