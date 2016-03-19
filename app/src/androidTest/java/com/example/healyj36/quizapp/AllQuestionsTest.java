package com.example.healyj36.quizapp;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import android.graphics.Color;
import android.support.test.espresso.intent.Checks;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.hamcrest.Matcher;
import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

/**
 * Created by Jordan on 18/03/2016.
 */

@RunWith(AndroidJUnit4.class)
public class AllQuestionsTest {

    @Rule
    public final ActivityTestRule<AllQuestions> main = new ActivityTestRule<>(AllQuestions.class);

    // test whether activity opens the chosen question
    @Test
    public void open_chosen_question() {
        String ques = "Which material is most dense?";
        onData(hasToString(startsWith(ques)))
                .inAdapterView(withId(android.R.id.list)).atPosition(0)
                .perform(click());
        onView(withId(R.id.question_text_view)).check(matches(withText(ques)));
    }

    // test whether buttons get right or correct answer
    // also proves there's is only ine correct answer
    @Test
    public void get_answer() {
        String t = "true";
        String f = "false";
        open_chosen_question();
        // we know option4 is correct answer
        // option 4 will be true
        // the rest will be false

        onView(withId(R.id.option1_button_text_view)).perform(click());
        onView(withId(R.id.chosen_answer_text_view)).check(matches(withText(f)));
        onView(withId(R.id.option2_button_text_view)).perform(click());
        onView(withId(R.id.chosen_answer_text_view)).check(matches(withText(f)));
        onView(withId(R.id.option3_button_text_view)).perform(click());
        onView(withId(R.id.chosen_answer_text_view)).check(matches(withText(f)));
        onView(withId(R.id.option4_button_text_view)).perform(click());
        onView(withId(R.id.chosen_answer_text_view)).check(matches(withText(t)));
    }

    // test whether text color from style is played
    @Test
    public void test_style() {
        open_chosen_question();
        // text colour should be white (#FFFFFF)
        onView(withId(R.id.option1_button_text_view)).check(matches(withTextColor(Color.parseColor("#FFFFFF"))));
    }

    public static Matcher<View> withTextColor(final int color) {
        Checks.checkNotNull(color);
        return new BoundedMatcher<View, Button>(Button.class) {
            @Override
            public boolean matchesSafely(Button warning) {
                return color == warning.getCurrentTextColor();
            }
            @Override
            public void describeTo(Description description) {
                description.appendText("with text color: ");
            }
        };
    }
}