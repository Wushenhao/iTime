package com.example.itime.ui.colors;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ColorsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ColorsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("选择颜色");
    }

    public LiveData<String> getText() {
        return mText;
    }
}