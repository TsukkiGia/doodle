package com.example.oneinamillion.Models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONArray;
import org.parceler.Parcel;

@Parcel
@Entity
public class EventForSaving {
    public EventForSaving() {
    }

    @NonNull
    @ColumnInfo
    @PrimaryKey
    public String eventID;

    @ColumnInfo
    public String eventName;

    @ColumnInfo
    public String eventDescription;

    @ColumnInfo
    public String eventDate;

    @ColumnInfo
    public String eventTime;

    @ColumnInfo
    public String eventAddress;

    @ColumnInfo
    public String eventAttendees;

    @ColumnInfo
    public double eventLatitude;

    @ColumnInfo
    public double eventLongitude;

    public double getEventLatitude() {
        return eventLatitude;
    }

    public void setEventLatitude(double eventLatitude) {
        this.eventLatitude = eventLatitude;
    }

    public double getEventLongitude() {
        return eventLongitude;
    }

    public void setEventLongitude(double eventLongitude) {
        this.eventLongitude = eventLongitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @ColumnInfo
    public double distance;

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }

    public String getEventAttendees() {
        return eventAttendees;
    }

    public void setEventAttendees(String eventAttendees) {
        this.eventAttendees = eventAttendees;
    }

    public String getEventOrganizerID() {
        return eventOrganizerID;
    }

    public void setEventOrganizerID(String eventOrganizerID) {
        this.eventOrganizerID = eventOrganizerID;
    }

    @ColumnInfo
    public String eventOrganizerID;

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
}
