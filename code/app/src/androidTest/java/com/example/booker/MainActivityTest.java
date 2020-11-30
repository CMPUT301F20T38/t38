package com.example.booker;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.booker.activities.UserLogin;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class MainActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<MainActivity>(MainActivity.class, true, true);

    @Before
    public void setUp() {
        solo = new  Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

    }

    @Test
    public void Test(){



    }


    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }
}
