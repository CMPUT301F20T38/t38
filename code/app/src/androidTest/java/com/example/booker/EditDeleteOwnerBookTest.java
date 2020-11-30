package com.example.booker;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.booker.activities.EditDeleteOwnerBook;
import com.example.booker.activities.ImagesActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

public class EditDeleteOwnerBookTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<EditDeleteOwnerBook> rule =
            new ActivityTestRule<EditDeleteOwnerBook>(EditDeleteOwnerBook.class, true, true);

    @Before
    public void setUp(){
        solo = new  Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }
}
