package com.example.itime.ui.timing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.itime.ItimeMainActivity;

public class TimingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TimingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}