package com.example.booker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.booker.R;

public class ChangeProfile extends AppCompatActivity {

    private EditText changeUserNamne;
    private Button cancelBtn;
    private Button comfirmBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_profile);

        changeUserNamne = (EditText) findViewById(R.id.change_userName);
        comfirmBtn = (Button) findViewById(R.id.change_confirm_btn);
        cancelBtn = (Button) findViewById(R.id.change_cancel_btn);

        final Intent intent = getIntent();
        String currrentUserName = intent.getStringExtra("User Name");
        changeUserNamne.setText(currrentUserName);

        comfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName = changeUserNamne.getText().toString();
                intent.putExtra("New Name", newName);
                setResult(0, intent);
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
