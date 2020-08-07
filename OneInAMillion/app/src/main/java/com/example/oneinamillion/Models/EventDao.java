package com.example.oneinamillion.Models;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM EventForSaving LIMIT 300")
    List<EventForSaving> recentItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertEvent(EventForSaving event);

    @Query("DELETE FROM EventForSaving")
    public void nukeTable();
}
