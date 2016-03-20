package com.example.healyj36.quizapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jordan on 19/02/2016.
 */
public class DBFunc extends SQLiteOpenHelper {

    private final static String DB_PATH = "/data/data/com.example.healyj36.quizapp/databases/";

    private final static String DB_NAME = "questions.db";

    private static int DB_VERSION = 22;
    private static final String TB_NAME1 = "questions";
    private static final String TB_NAME2 = "answers";

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
        DB_VERSION++;
    }

    @Override
    public synchronized void close() {
        if (myDB != null) {
            myDB.close();
        }
        super.close();
    }

    //Copy database from source code assets to device
    private void copyDataBase() throws IOException {
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
            Log.e("DBFunc - copyDatabase", e.getMessage());
        }
    }

    public void createDatabase() throws IOException {
        // Don't need to check if db exits
        // Game won't load without it
        // There's nothing we can do with this information

        this.getReadableDatabase();
        try {
            copyDataBase();

        } catch (IOException e) {
            Log.e("DBFunc - createDB", e.getMessage());
        }
    }

    public ArrayList<HashMap<String, String>> getQuestionsRandom(int numberOfQuestions, String category) {
        ArrayList<HashMap<String, String>> questionArrayList = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TB_NAME1 + " WHERE category LIKE '" + category + "' ORDER BY RANDOM() LIMIT " + numberOfQuestions;
        if(category.equals("All Subjects")) {
            selectQuery = "SELECT * FROM " + TB_NAME1 + " ORDER BY RANDOM() LIMIT " + numberOfQuestions;
        }
        // SELECT * FROM questions WHERE category LIKE 'Geography' ORDER BY RANDOM() LIMIT 2;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery(selectQuery, null);
        if (c.moveToFirst()) {
            do {
                HashMap<String, String> questionMap = new HashMap<>();

                questionMap.put("questionId", c.getString(0));
                questionMap.put("question", c.getString(1));
                questionMap.put("option1", c.getString(2));
                questionMap.put("option2", c.getString(3));
                questionMap.put("option3", c.getString(4));
                questionMap.put("option4", c.getString(5));
                //questionMap.put("subject", cursor.getString(6));
                // not needed

                questionArrayList.add(questionMap);
            } while (c.moveToNext());
        }
        db.close();
        c.close();
        return questionArrayList;
    }

    public ArrayList<String> getAllQuestionNames() {
        ArrayList<String> listQuestion = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TB_NAME1;
        Cursor c = db.rawQuery(selectQuery, null);

        if(c.moveToFirst()) {
            do {
                String q = c.getString(1);
                listQuestion.add(q);
            } while(c.moveToNext());
        }
        db.close();
        c.close();
        return listQuestion;
    }

    public ArrayList<String> findOptionsByQuestion(String ques) {
        ArrayList<String> listOptions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TB_NAME1 + " WHERE question='" + ques + "'";
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
        c.close();
        return listOptions;
    }

    public boolean isAnswer(String ques, final String chosenAnswer) {
        boolean isAnswer;
        String correctAnswer = getAnswer(ques);

        isAnswer = (correctAnswer.equals(chosenAnswer));
        return isAnswer;
    }

    public String getAnswer(String ques) {
        SQLiteDatabase db = this.getWritableDatabase();
        String queryForId = "SELECT questionId FROM " + TB_NAME1 + " WHERE question='" + ques + "'";
        // e.g. SELECT questionId FROM questions WHERE question='Which material is most dense?'
        if (db == null) {
            return "Gold";
        }
        Cursor c = db.rawQuery(queryForId, null);
        String id = "none";
        // if id is not changed to the questionId value, then queryForAnswer will have no matches in db

        if(c.moveToFirst()) {
            id = c.getString(0);
        } while(c.moveToNext());

        String queryForAnswer = "SELECT answer FROM " + TB_NAME2 + " WHERE questionId='" + id + "'";
        // e.g. SELECT answer FROM answers WHERE questionId='1'

        c = db.rawQuery(queryForAnswer, null);
        String correctAnswer = null;
        if(c.moveToFirst()) {
            correctAnswer = c.getString(0);
            // (1) as second column
            // first column is questionId
            // second column is answer
        } while(c.moveToNext());

        db.close();
        c.close();

        return correctAnswer;
    }

    public int getTotalNumberOfQuestions(String category) {
        String selectQuery = "SELECT COUNT(*) FROM " + TB_NAME1 + " WHERE category LIKE '" + category + "'";
        if(category.equals("All Subjects")) {
            selectQuery = "SELECT COUNT(*) FROM " + TB_NAME1;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        int ans = -1; // returns -1 if query unsuccessful
        if (c.moveToFirst()) {
            ans = c.getInt(0);
        }

        db.close();
        c.close();

        return ans;
    }

    public int getNumberOfQuestionsBySubject(String category) {
        String selectQuery = "SELECT COUNT(*) FROM " + TB_NAME1 + " WHERE category LIKE'" + category +"'";
        if(category.equals("All Subjects")){
            selectQuery = "SELECT COUNT(*) FROM questions";
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        int ans = -1; // returns -1 if query unsuccessful
        if (c.moveToFirst()) {
            ans = c.getInt(0);
        }

        db.close();
        c.close();

        return ans;
    }

    public ArrayList<String> getSubjects() {
        ArrayList<String> allSubjects = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT category FROM questions";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        String q;
        if(c.moveToFirst()) {
            do {
                q = c.getString(0);
                allSubjects.add(q);
            } while(c.moveToNext());
        }
        db.close();
        c.close();
        return allSubjects;
    }
}