package com.example.booker.ui.borrow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.booker.R;
import com.example.booker.activities.ChangeProfile;
import com.example.booker.activities.UserLogin;
import com.example.booker.data.User;


public class BorrowFragment extends Fragment {

    private Button btnLogin;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState) {
        View root = inflater.inflate(R.layout.fragment_borrow, container, false);
        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnLogin = (Button) getActivity().findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), UserLogin.class);
                startActivity(intent);
            }
        });
    }
}
