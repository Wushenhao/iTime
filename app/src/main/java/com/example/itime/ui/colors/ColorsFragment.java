package com.example.itime.ui.colors;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.itime.R;

public class ColorsFragment extends Fragment {

    private ColorsViewModel colorsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        colorsViewModel =
                ViewModelProviders.of(this).get(ColorsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_colors, container, false);
        final TextView textView = root.findViewById(R.id.text_colors);
        colorsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}