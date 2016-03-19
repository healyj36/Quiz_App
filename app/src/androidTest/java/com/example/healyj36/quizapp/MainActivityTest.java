package com.example.healyj36.quizapp;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Jordan on 18/03/2016.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void test_text_in_button() {
        onView(withId(R.id.online_1v1)).check(ViewAssertions.matches(withText(R.string.online_game_mode_title)));
        onView(withId(R.id.infinite_game_mode)).check(ViewAssertions.matches(withText(R.string.infinite_game_mode_text_view)));
        onView(withId(R.id.all_questions_game_mode)).check(ViewAssertions.matches(withText(R.string.all_questions_text_view)));
        onView(withId(R.id.settings)).check(ViewAssertions.matches(withText(R.string.chat_room_title)));
    }
}
