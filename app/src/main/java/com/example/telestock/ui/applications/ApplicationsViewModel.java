package com.example.massage_parlor.ui.applications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ApplicationsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ApplicationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
