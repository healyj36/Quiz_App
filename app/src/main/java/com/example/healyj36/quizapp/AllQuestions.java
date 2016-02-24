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

/**
 * Created by Jordan on 23/02/2016.
 */
public class AllQuestions extends ListActivity {
    Intent intent; // TODO delete. maybe not needed
    TextView questionId; // TODO delete. maybe not needed
    ListView lvQuestions;

    DBFunc dbFunc = new DBFunc(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_questions);

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
                    Intent viewQues = new Intent(AllQuestions.this, ViewQuestion.class);

                    // final int result = 1; // signal

                    String questionName = (String) parent.getItemAtPosition(position);

                    viewQues.putExtra("questionKey", questionName);

                    // call activity to run and don't expect data to be sent back
                    startActivity(viewQues);

                    // call activity to run and retrieve data back
                    // startActivityForResult(viewQues, result);
                }
            });
        }
    }
}
