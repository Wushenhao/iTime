package com.example.itime.ui.tag;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TagViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TagViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tag fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}