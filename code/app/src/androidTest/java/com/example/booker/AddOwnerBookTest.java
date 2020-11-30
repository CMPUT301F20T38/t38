package com.example.booker;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.booker.activities.AddOwnerBook;
import com.example.booker.ui.lend.LendFragment;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AddOwnerBookTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<AddOwnerBook> rule =
            new ActivityTestRule<AddOwnerBook>(AddOwnerBook.class, true, true);

    @Before
    public void setUp(){
        solo = new  Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    @Test
    public void confirm(){
        solo.enterText((EditText) solo.getView(R.id.owner_add_title), "UI_TEST");
        solo.enterText((EditText) solo.getView(R.id.owner_add_author), "UI_TEST");
        solo.enterText((EditText) solo.getView(R.id.owner_add_ISBN), "UI_TEST");


        solo.clickOnButton("Confirm");
        solo.waitForFragmentById(R.layout.fragment_lend, 1000);




    }

    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }

}
