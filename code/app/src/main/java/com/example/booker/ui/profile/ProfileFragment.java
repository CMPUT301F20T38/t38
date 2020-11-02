package com.example.booker.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.booker.MainActivity;
import com.example.booker.R;
import com.example.booker.activities.ChangeProfile;


public class ProfileFragment extends Fragment {

    private LinearLayout changeProfile;
    private TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_profile, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeProfile = (LinearLayout) getActivity().findViewById(R.id.user_profile);
        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ChangeProfile.class);
                textView = (TextView) getActivity().findViewById(R.id.user_name);
                String userName = textView.getText().toString();
                intent.putExtra("User Name", userName);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode == 0){
                textView.setText(data.getStringExtra("New Name"));
            }
        }
    }
}
