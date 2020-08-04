package com.example.oneinamillion.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.parceler.Parcel;

@ParseClassName("Post")
@Parcel(analyze={Post.class})
public class Post extends ParseObject {
    public static final String KEY_AUTHOR="Author";
    public static final String KEY_IMAGE="Image";
    public static final String KEY_DESCRIPTION="Description";
    public static final String KEY_EVENT="EventID";
    public static final String KEY_LIKERS="Likers";
    public static final String KEY_VIDEO="Video";

    public Post() {
    }

    public JSONArray getLikers(){
        return getJSONArray(KEY_LIKERS);
    }

    public void setLikers(JSONArray Likes) {
        put(KEY_LIKERS,Likes);
    }

    public String getEventID() {
        return getString(KEY_EVENT);
    }

    public void setEventID(String EventID){
        put(KEY_EVENT,EventID);
    }

    public ParseUser getAuthor() {
        return getParseUser(KEY_AUTHOR);
    }

    public void setAuthor(ParseUser parseUser) {
        put(KEY_AUTHOR,parseUser);
    }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION,description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ParseFile getVideo() {
        return getParseFile(KEY_VIDEO);
    }

    public void setVideo(ParseFile parseFile) {
        put(KEY_VIDEO, parseFile);
    }

}
