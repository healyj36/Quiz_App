package com.example.healyj36.quizapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import java.io.IOException;

public class DBFuncTest extends AndroidTestCase{
    DBFunc db;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        RenamingDelegatingContext context = new RenamingDelegatingContext(getContext(), "test_");
        db = new DBFunc(context);

        try {
            db.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*
    @SmallTest
    public void test_isAnswer() throws Exception{
        boolean answer = db.isAnswer("Which material is most dense?", "Gold");
        assertTrue(answer);
    }
    */

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        db.close();
    }
}