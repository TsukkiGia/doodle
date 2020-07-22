package com.example.oneinamillion;

import android.app.Application;

import com.example.oneinamillion.Models.Event;
import com.example.oneinamillion.Models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Post.class);
        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("one-in-a-million") // should correspond to APP_ID env variable
                .clientKey("JinIsLovelyLovelyLovely")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://one-in-a-million.herokuapp.com/parse/").build());
    }
}
