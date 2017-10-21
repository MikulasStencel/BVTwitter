package com.epicqueststudios.bvtwitter.feature.sqlite;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/***
 * Class is used for multi-threading safe access to database
 */

public class DatabaseManager
{
    private AtomicInteger                openCounter = new AtomicInteger();

    private static DatabaseManager       instance;
    private static DatabaseHandler       databaseHandler;
    private SQLiteDatabase               database;

    public DatabaseManager()
    {

    }

    public static synchronized void initializeInstance(DatabaseHandler helper)
    {
        if( instance == null )
        {
            instance = new DatabaseManager();
            databaseHandler = helper;
        }
    }

    public static synchronized DatabaseManager getInstance()
    {
        if( instance == null )
        {
            throw new IllegalStateException(DatabaseManager.class.getSimpleName()
                    + " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase(boolean bWrite)
    {
        if( openCounter.incrementAndGet() == 1 )
        {
            database = (bWrite) ? databaseHandler.getWritableDatabase() : databaseHandler.getReadableDatabase();
        }
        return database;
    }

    public synchronized void closeDatabase()
    {
        if( openCounter.decrementAndGet() == 0 )
        {
            if( database != null )
                database.close();

        }
    }

}
