package com.example.booker.ui.profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<String> userName;

    public ProfileViewModel() {
        userName = new MutableLiveData<>();
        userName.setValue("");
    }

    public MutableLiveData<String> getUserName() {
        return userName;
    }

    public void setUserName(String newUserEmail) {
        userName.setValue(newUserEmail);
    }
}
