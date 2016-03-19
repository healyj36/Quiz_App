package com.example.healyj36.quizapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jordan on 23/02/2016.
 */
public class AllQuestions extends ListActivity {

    private final DBFunc DB_FUNC = new DBFunc(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_questions);

        try {
            DB_FUNC.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> allQuestionNames = DB_FUNC.getAllQuestionNames();
        // Populate ListView with all questions in db
        if(allQuestionNames.size() != 0) {
            ListView lvQuestions = getListView();

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,allQuestionNames
            );

            lvQuestions.setAdapter(adapter);

            // When item is clicked, go to that question
            lvQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent viewQues = new Intent(AllQuestions.this, ViewQuestion.class);

                    // final int result = 1; // signal
                    String questionName = (String) parent.getItemAtPosition(position);

                    viewQues.putExtra("questionKey", questionName);

                    // call activity to run and don't expect data to be sent back
                    startActivity(viewQues);
                }
            });
        }
    }

}
