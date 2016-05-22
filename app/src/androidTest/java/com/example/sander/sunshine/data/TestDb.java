package com.example.sander.sunshine.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import java.util.HashSet;


public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.
        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
   public void testCreateDb() throws Throwable {

       // Students:  Here is where you will build code to test that we can insert and query the
       // location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
       // where you can uncomment out the "createNorthPoleLocationValues" function.  You can
       // also make use of the ValidateCurrentRecord function from within TestUtilities.
   }
    public void testLocationTable() {
        // First step: Get reference to writable database
        insertLocation();

        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)




        // Insert ContentValues into database and get a row ID back

        // Query the database and receive a Cursor back


        // Move the cursor to a valid database row

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)

        // Finally, close the cursor and database

    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testWeatherTable() {
        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.
 long locationRowId=insertLocation();
        // Instead of rewriting all of the code we've already written in testLocationTable
        // we can move this code to insertLocation and then call insertLocation from both
        // tests. Why move it? We need the code to return the ID of the inserted location
        // and our testLocationTable can only return void because it's a test.

        // First step: Get reference to writable database
mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase database=new WeatherDbHelper(mContext).getWritableDatabase();
        assertEquals(true,database.isOpen());
        // Create ContentValues of what you want to insert
        // (you can use the createWeatherValues TestUtilities function if you wish)
ContentValues values=TestUtilities.createWeatherValues(locationRowId);
        // Insert ContentValues into database and get a row ID back
locationRowId=database.insert(WeatherContract.WeatherEntry.TABLE_NAME,null,values);
        assertTrue("Error",locationRowId!=-1);
        // Query the database and receive a Cursor back
Cursor cursor=database.query(WeatherContract.WeatherEntry.TABLE_NAME,null,null,null,null,null,null);
        // Move the cursor to a valid database row
assertTrue("Error:",cursor.moveToNext());
        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
TestUtilities.validateCurrentRecord("Error", cursor, values);
        // Finally, close the cursor and database
        assertFalse("Error:",cursor.moveToNext());
        cursor.close();
        database.close();

    }


    /*
        Students: This is a helper method for the testWeatherTable quiz. You can move your
        code from testLocationTable to here so that you can call this code from both
        testWeatherTable and testLocationTable.
     */
    public long insertLocation() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase database=new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, database.isOpen());
        ContentValues values=TestUtilities.createNorthPoleLocationValues();
        long locationRowId;
        locationRowId=database.insert(WeatherContract.LocationEntry.TABLE_NAME,null,values);
        assertTrue("Error:Failure to insert Location", locationRowId != -1);
        Cursor cursor=database.query(WeatherContract.LocationEntry.TABLE_NAME,null,null,null,null,null,null);
        assertTrue("Error",cursor.moveToFirst());
        TestUtilities.validateCurrentRecord("Error:validation failed", cursor, values);
        assertFalse("error", cursor.moveToNext());
        cursor.close();
        database.close();
        return locationRowId;

    }
}

