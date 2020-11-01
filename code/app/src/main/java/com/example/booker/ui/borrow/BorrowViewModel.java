package com.example.booker.ui.borrow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BorrowViewModel extends ViewModel {
    private MutableLiveData<String> bText;

    public BorrowViewModel() {
        bText = new MutableLiveData<>();
        bText.setValue("Main Page");
    }

    public LiveData<String> getText() {
        return bText;
    }

}
