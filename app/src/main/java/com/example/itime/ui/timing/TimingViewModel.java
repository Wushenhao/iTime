package com.example.itime.ui.timing;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.itime.ItimeMainActivity;

public class TimingViewModel extends ViewModel {

    private MutableLiveData<ItimeMainActivity.DateArrayAdapter> mAdapter;

    public LiveData<ItimeMainActivity.DateArrayAdapter> getAdapter() {
        if (mAdapter == null){
            mAdapter = new MutableLiveData<>();
        }
        return mAdapter;
    }
}