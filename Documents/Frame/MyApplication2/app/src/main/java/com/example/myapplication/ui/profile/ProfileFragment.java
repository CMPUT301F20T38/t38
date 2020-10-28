package com.example.myapplication.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.subactivity.ChangeProfile;


public class ProfileFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        LinearLayout changeProfile = (LinearLayout) root.findViewById(R.id.user_profile);
        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChangeProfile.class);

            }
        });
        return root;
    }
}
