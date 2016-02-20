package com.example.healyj36.quizapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {
    Intent intent;
    TextView questionId;

    DBFunc dbFunc = new DBFunc(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayList<HashMap<String,String>> questionList = dbFunc.getAllQuestions();

        if(questionList.size() != 0) {
            ListView listView = getListView();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    questionId = (TextView) view.findViewById(R.id.questionId);
                    String questionIdValue = questionId.getText().toString();

                    Intent theIntent = new Intent(getApplication(), ViewQuestion.class);

                    theIntent.putExtra("questionId", questionIdValue);

                    startActivity(theIntent);
                }
            });
        }

        ListAdapter adapter = new SimpleAdapter(
                MainActivity.this, questionList, R.layout.question_entry,
                new String[] {"questionId", "question"},
                new int[] {R.id.questionId, R.id.question});
        setListAdapter(adapter);
    }
    /*
    public void getAnswer(){

    }
    */


}
