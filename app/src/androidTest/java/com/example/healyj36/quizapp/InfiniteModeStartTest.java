package com.example.healyj36.quizapp;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

/**
 * Created by Jordan on 18/03/2016.
 */

@RunWith(AndroidJUnit4.class)
public class InfiniteModeStartTest {
    ArrayList<String> subjects = new ArrayList<>();
    ArrayList<Integer> maxNumber= new ArrayList<>();

    @Rule
    public final ActivityTestRule<InfiniteModeStart> main = new ActivityTestRule<>(InfiniteModeStart.class);

    @Test
    public void is_subjects_spinner_populated() {
        subjects.add("All Subjects");
        subjects.add("Physics");
        subjects.add("Geography");
        subjects.add("History");
        for(int i=0; i<subjects.size(); i++) {
            String sub = subjects.get(i);
            onView(withId(R.id.spinner_subjects)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is(sub))).perform(click());
            onView(withText(sub)).check(matches(withText(containsString(sub))));
        }
    }

    @Test
    public void is_number_of_questions_spinner_populated() {
        /// add numbers 1 to 8, in that order
        for(int i=1; i<=8; i++) {
            maxNumber.add(i);
        }

        // convert the values to Strings
        ArrayList<String> maxNumberStrings = new ArrayList<>(maxNumber.size());
        for(Integer myInt : maxNumber) {
            maxNumberStrings.add(String.valueOf(myInt));
        }
        // add "All Questions" to the end
        maxNumberStrings.add("All Questions");

        String sub = "All Questions";
        // click value sub in subjects spinner
        onView(withId(R.id.spinner_subjects)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is(sub))).perform(click());

        for(int j=0; j<maxNumberStrings.size(); j++) {
            onView(withId(R.id.number_of_questions)).perform(click());
            onData(allOf(is(instanceOf(String.class)), is(maxNumberStrings.get(j)))).perform(click());
            onView(withText(maxNumberStrings.get(j))).check(matches(withText(containsString(maxNumberStrings.get(j)))));
        }
    }
}