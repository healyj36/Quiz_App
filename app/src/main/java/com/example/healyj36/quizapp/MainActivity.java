package com.example.healyj36.quizapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {
    Intent intent; // maybe not needed
    TextView questionId; // maybe not needed
    ListView lvQuestions;

    DBFunc dbFunc = new DBFunc(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            dbFunc.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<HashMap<String, String>> listQuestions = dbFunc.getAllQuestions();

        ArrayList<String> allQuestionNames = dbFunc.getAllQuestionNames();
        if(listQuestions.size() != 0){
            lvQuestions = getListView();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,allQuestionNames
            );

            lvQuestions.setAdapter(adapter);

            lvQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO open next page with question and options
                }
            });
        }
    }
    /*
    public void getAnswer(){

    }
    */
}
