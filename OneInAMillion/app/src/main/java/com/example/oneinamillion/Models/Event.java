package com.example.oneinamillion.Models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.parceler.Parcel;

import java.util.Date;

@ParseClassName("Event")
@Parcel(analyze={Event.class})
public class Event extends ParseObject {
    public static final String KEY_LOCATION = "Location";
    public static final String KEY_DATE = "Date";
    public static final String KEY_TIME = "Time";
    public static final String KEY_DESCRIPTION = "Description";
    public static final String KEY_IMAGE = "EventImage";
    public static final String KEY_ATTENDEES = "Attendees";
    public static final String KEY_ORGANIZER = "Organizer";
    public static final String KEY_EVENTNAME = "EventName";

    public Event() {
    }

    public String getEventName(){
        return getString(KEY_EVENTNAME);
    }

    public void setEventName(String eventName) {
        put(KEY_EVENTNAME,eventName);
    }

    public JSONArray getAttendees(){
        return getJSONArray(KEY_ATTENDEES);

    }

    public void setAttendees(JSONArray Attendees) {
        put(KEY_ATTENDEES,Attendees);
    }

    public ParseGeoPoint getLocation(){
        return getParseGeoPoint(KEY_LOCATION);
    }

    public void setLocation(ParseGeoPoint geoPoint){
        put(KEY_LOCATION,geoPoint);
    }

    public String getDate(){
        return getString(KEY_DATE);
    }

    public void setDate(String date){
        put(KEY_DATE,date);
    }

    public String getTime(){
        return getString(KEY_TIME);
    }

    public void setTime(String time){
        put(KEY_TIME,time);
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

    public ParseUser getOrganizer() {
        return getParseUser(KEY_ORGANIZER);
    }

    public void setOrganizer(ParseUser parseUser) {
        put(KEY_ORGANIZER,parseUser);
    }
}