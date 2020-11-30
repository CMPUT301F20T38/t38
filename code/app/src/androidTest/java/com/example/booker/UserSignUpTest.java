package com.example.booker;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.booker.activities.ImagesActivity;
import com.example.booker.activities.UserSignUp;
import com.google.firebase.firestore.auth.User;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

public class UserSignUpTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<UserSignUp> rule =
            new ActivityTestRule<UserSignUp>(UserSignUp.class, true, true);

    @Before
    public void setUp(){
        solo = new  Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }
}
