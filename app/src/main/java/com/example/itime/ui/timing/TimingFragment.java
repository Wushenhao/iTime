package com.example.itime.ui.timing;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.itime.ItimeMainActivity;
import com.example.itime.R;
import com.example.itime.model.Date;

import java.util.ArrayList;
import java.util.List;

public class TimingFragment extends Fragment {
    private TimingViewModel timingViewModel;

    ItimeMainActivity.DateArrayAdapter dateArrayAdapter;

    public TimingFragment(ItimeMainActivity.DateArrayAdapter dateArrayAdapter) {
        this.dateArrayAdapter = dateArrayAdapter;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        timingViewModel =
                ViewModelProviders.of(this).get(TimingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_timing, container, false);
        final TextView textView = root.findViewById(R.id.text_timing);
        timingViewModel.getAdapter().observe(this, new Observer<ItimeMainActivity.DateArrayAdapter>() {
            @Override
            public void onChanged(@Nullable ItimeMainActivity.DateArrayAdapter adapter) {
                dateArrayAdapter = adapter;
            }
        });


        //dateArrayAdapter = (ItimeMainActivity.DateArrayAdapter) getArguments().getSerializable("theAdapter");

        ListView listViewSuper = (ListView) root.findViewById(R.id.list_view_timing);
        listViewSuper.setAdapter(dateArrayAdapter);

        this.registerForContextMenu(listViewSuper);
        return root;
    }
}