package com.example.oneinamillion.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("Post")
@Parcel(analyze={Post.class})
public class Post extends ParseObject {
    public static final String KEY_AUTHOR="Author";
    public static final String KEY_IMAGE="Image";
    public static final String KEY_DESCRIPTION="Description";
    public static final String KEY_EVENT="EventID";

    public Post() {
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

}
