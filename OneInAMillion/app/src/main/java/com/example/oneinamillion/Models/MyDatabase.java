package com.example.oneinamillion.Models;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities={EventForSaving.class}, version=5)
public abstract class MyDatabase extends RoomDatabase {
    // Declare your data access objects as abstract
    public abstract EventDao eventDao();

    // Database name to be used
    public static final String NAME = "MyDataBase";
}
