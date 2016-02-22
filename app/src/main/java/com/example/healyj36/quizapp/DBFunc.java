package com.example.healyj36.quizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jordan on 19/02/2016.
 */
public class DBFunc extends SQLiteOpenHelper {

    public static String DB_PATH = "/data/data/com.example.healyj36.quizapp/databases/";

    public static String DB_NAME = "questions.db";
    public static final int DB_VERSION = 1;
    public static final String TB_NAME = "questions";

    private SQLiteDatabase myDB;
    private Context context;

    public DBFunc(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public synchronized void close() {
        if (myDB != null) {
            myDB.close();
        }
        super.close();
    }

    // Check if the database is exist on device or not
    // TODO delete checkDatabase(). Not used
    private boolean checkDatabase() {
        SQLiteDatabase tempDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            tempDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        } catch (SQLiteException e) {
            // TODO change log message
            Log.e("tle99 - check", e.getMessage());
        }
        if (tempDB != null)
            tempDB.close();
        return tempDB != null ? true : false;
    }

    //Copy database from source code assets to device
    public void copyDataBase() throws IOException {
        try {
            InputStream myInput = context.getAssets().open(DB_NAME);
            String outputFileName = DB_PATH + DB_NAME;
            OutputStream myOutput = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception e) {
            // TODO change log message
            Log.e("tle99 - copyDatabase", e.getMessage());
        }
    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    //Check if the database doesn't exist on device, create new one
    public void createDatabase() throws IOException {
        // TODO probably should check if db exists

        /*
        boolean dbExist = checkDataBase();

        if (dbExist) {

        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                Log.e("tle99 - create", e.getMessage());
            }
        }
        */
        this.getReadableDatabase();
        try {
            copyDataBase();
        } catch (IOException e) {
            // TODO change log message
            Log.e("tle99 - create", e.getMessage());
        }
    }

    //public ArrayList<String> getAllUsers(){
    public ArrayList<HashMap<String, String>> getAllQuestions() {
        /*
        ArrayList<String> listQuestion = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c;

        try {
            c = db.rawQuery("SELECT * FROM " + TB_NAME , null);
            if(c == null) return null;

            String q;
            c.moveToFirst();
            do {
                q = c.getString(1);
                listQuestion.add(q);
            } while (c.moveToNext());
            c.close();
        } catch (Exception e) {
            // TODO change log message
            Log.e("tle99", e.getMessage());
        }
        db.close();
        return listQuestion;
        */
        ArrayList<HashMap<String, String>> questionArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM questions ORDER BY questionId";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> questionMap = new HashMap<String, String>();

                questionMap.put("questionId", cursor.getString(0));
                questionMap.put("question", cursor.getString(1));
                questionMap.put("option1", cursor.getString(2));
                questionMap.put("option2", cursor.getString(3));
                questionMap.put("option3", cursor.getString(4));
                questionMap.put("option4", cursor.getString(5));

                questionArrayList.add(questionMap);
            } while (cursor.moveToNext());
        }
        return questionArrayList;
    }

    public HashMap<String, String> getQuestionInfo(String id) {
        HashMap<String, String> questionMap = new HashMap<String, String>();

        // Open a database for reading only

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM questions WHERE questionId='" + id + "'";

        // rawQuery executes the query and returns the result as a Cursor

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                questionMap.put("questionId", cursor.getString(0));
                questionMap.put("question", cursor.getString(1));
                questionMap.put("option1", cursor.getString(2));
                questionMap.put("option2", cursor.getString(3));
                questionMap.put("option3", cursor.getString(4));
                questionMap.put("option4", cursor.getString(5));
            } while (cursor.moveToNext());
        }
        return questionMap;
    }

    public ArrayList<String> getAllQuestionNames() {
        ArrayList<String> listQuestion = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TB_NAME;
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()) {
            do {
                String q = c.getString(1);
                listQuestion.add(q);
            } while(c.moveToNext());
        }
        db.close();
        return listQuestion;
    }

    public ArrayList<String> findOptionsByQuestion(String ques) {
        ArrayList<String> listOptions = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TB_NAME + " WHERE question='" + ques + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        String q;

        if(c.moveToFirst()) {
            do {
                q = c.getString(2);
                listOptions.add(q);
                q = c.getString(3);
                listOptions.add(q);
                q = c.getString(4);
                listOptions.add(q);
                q = c.getString(5);
                listOptions.add(q);
            } while(c.moveToNext());
        }
        db.close();
        return listOptions;
    }
}