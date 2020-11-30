package com.example.booker;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.booker.activities.AddOwnerBook;
import com.example.booker.activities.UserLogin;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<UserLogin> rule =
            new ActivityTestRule<UserLogin>(UserLogin.class, true, true);

    @Before
    public void setUp(){
        solo = new  Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    @Test
    public void test(){
        solo.enterText((EditText) solo.getView(R.id.login_email), "UI_TEST");
        solo.enterText((EditText) solo.getView(R.id.login_password), "UI_TEST");
        solo.clickOnButton("Sign in or register");


    }

    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }
}
