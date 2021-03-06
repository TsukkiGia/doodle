package com.example.oneinamillion.Models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.parceler.Parcel;

@ParseClassName("Comment")
@Parcel(analyze={Comment.class})
public class Comment extends ParseObject {
    public static final String KEY_POST = "PostID";
    public static final String KEY_USER = "Commenter";
    public static final String KEY_TEXT = "CommentText";
    public static final String KEY_LIKERS = "Likers";

    public Comment() {
    }

    public JSONArray getLikers(){
        return getJSONArray(KEY_LIKERS);
    }

    public void setLikers(JSONArray Likes) {
        put(KEY_LIKERS,Likes);
    }

    public String getPostRelation() {
        return getString(KEY_POST);
    }

    public void setPost(String post) {
        put(KEY_POST,post);
    }

    public ParseUser getCommenter() {
        return getParseUser(KEY_USER);
    }

    public void setCommenter(ParseUser parseUser) {
        put(KEY_USER,parseUser);
    }

    public String getText() {
        return getString(KEY_TEXT);
    }

    public void setText(String string) {
        put(KEY_TEXT,string);
    }
}
