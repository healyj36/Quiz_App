package com.example.healyj36.quizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jordan on 19/02/2016.
 */
public class DBFunc extends SQLiteOpenHelper {

    public DBFunc(Context applicationContext) {
        super(applicationContext, "questions.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query = "CREATE TABLE questions ( questionId INTEGER PRIMARY KEY, question TEXT, option1 TEXT, " +
                "option2 TEXT, option3 TEXT, option4 TEXT )";

        // questionId could be = " questionId INTEGER PRIMARY KEY AUTOINCREMENT"
        // to automatically increment questionId when a question is added

        database.execSQL(query);

        /*
        if creating more than one table, create them inside this method
        may need to create a "answers" or maybe even a "leaderboard" table
        or three separate tables for "easy", "medium" or "hard"
        */
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS questions";
        database.execSQL(query);
        onCreate(database);
    }

    public void insertQuestion(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("question", queryValues.get("question"));
        values.put("option1", queryValues.get("option1"));
        values.put("option2", queryValues.get("option2"));
        values.put("option3", queryValues.get("option3"));
        values.put("option4", queryValues.get("option4"));

        database.insert("questions", null, values);

        database.close();
    }

    public int updateQuestion(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("question", queryValues.get("question"));
        values.put("option1", queryValues.get("option1"));
        values.put("option2", queryValues.get("option2"));
        values.put("option3", queryValues.get("option3"));
        values.put("option4", queryValues.get("option4"));

        return database.update("questions", values,
                "questionId" + " = ?", new String[] {queryValues.get("questionId")});
    }

    public void deleteQuestion(String id) {
        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FOM questions WHERE questionId='" + id + "'";

        database.execSQL(deleteQuery);
    }

    public ArrayList<HashMap<String, String>> getAllQuestions(){
        ArrayList<HashMap<String, String>> questionArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM questions ORDER BY questionId";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                HashMap<String,String> questionMap = new HashMap<String,String >();

                questionMap.put("questionId",cursor.getString(0));
                questionMap.put("question",cursor.getString(1));
                questionMap.put("option1",cursor.getString(2));
                questionMap.put("option2",cursor.getString(3));
                questionMap.put("option3",cursor.getString(4));
                questionMap.put("option4",cursor.getString(5));

                questionArrayList.add(questionMap);
            } while(cursor.moveToNext());
        }
        return questionArrayList;
    }

    public HashMap<String,String> getQuestionInfo(String id) {
        HashMap<String, String> questionMap = new HashMap<String,String>();
        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM questions WHERE questionId='" + id + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);
        if(cursor.moveToFirst()) {
            do{
                questionMap.put("questionId",cursor.getString(0));
                questionMap.put("question",cursor.getString(1));
                questionMap.put("option1",cursor.getString(2));
                questionMap.put("option2",cursor.getString(3));
                questionMap.put("option3",cursor.getString(4));
                questionMap.put("option4",cursor.getString(5));
            } while(cursor.moveToNext());
        }
        return questionMap;
    }
}
