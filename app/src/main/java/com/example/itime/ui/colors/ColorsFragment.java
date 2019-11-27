package com.example.itime.ui.colors;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.itime.MainActivity;
import com.example.itime.R;

import java.util.Objects;

public class ColorsFragment extends DialogFragment {
    private ColorsViewModel colorsViewModel;

    private SeekBar color_RGB;
    private static int hue,rgb = 0;
    private TextView int_rgb;

    public ColorsFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

        //constraintLayout = (ConstraintLayout) root;

        color_RGB = (SeekBar) root.findViewById(R.id.RGB);
        int_rgb = (TextView) root.findViewById(R.id.int_RGB);

        LinearGradient test = new LinearGradient(0.f, 0.f, 1000.f, 0.0f,new int[] {0xFFFF3333,0xFFFFFF33,0xFF33FF33,0xFF33FFFF,
                0xFF3333FF,0xFFFF33FF,0xFFFF3333},
                null, Shader.TileMode.CLAMP);

        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setShader(test);   //创建一个boxShape，然后在其中添加一个LinearGradient，实现进度条颜色渐变

        color_RGB.setProgressDrawable( (Drawable)shape );

        color_RGB.setMin(0);
        color_RGB.setMax(100);
        color_RGB.setProgress(0);

        color_RGB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress, boolean fromUser) {
                hue = seekBar.getProgress();
                rgb = hslToRgb((double)hue/100,0.8,0.6);

                String int_color_rgb= "RGB:" + String.valueOf(rgb);
                int_rgb.setText(int_color_rgb);
                ((MainActivity)  getActivity()).changeThemeColor(rgb);
                //toolBar.setBackgroundColor(Color.rgb(r,g,b));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                hue = seekBar.getProgress();
                rgb = hslToRgb((double)hue/100,0.8,0.6);

                String int_color_rgb= "RGB:" + String.valueOf(rgb);
                int_rgb.setText(int_color_rgb);
                ((MainActivity)  getActivity()).changeThemeColor(rgb);
                //toolBar.setBackgroundColor(Color.rgb(r,g,b));
            }
        });
        return root;
    }

    private int hslToRgb (double hue,double saturation,double luminosity){
        // int red, green, blue;
        double MaxRGB = 255.0;
        double b, g, r, v, x, y, z;
        int red,green,blue;
        /*
         * Convert HSL to RGB colorspace.
         */
        v = (luminosity <= 0.5) ? (luminosity * (1.0 + saturation))
                : (luminosity + saturation - luminosity * saturation);
        if (saturation == 0.0) {
            red = (int) (MaxRGB * luminosity + 0.5);
            green = (int) (MaxRGB * luminosity + 0.5);
            blue = (int) (MaxRGB * luminosity + 0.5);
            return Color.rgb(red,green,blue);
        }

        y = 2.0 * luminosity - v;
        x = y + (v - y) * (6.0 * hue - Math.floor(6.0 * hue));
        z = v - (v - y) * (6.0 * hue - Math.floor(6.0 * hue));
        switch ((int) (6.0 * hue)) {
            case 0:
                r = v;
                g = x;
                b = y;
                break;
            case 1:
                r = z;
                g = v;
                b = y;
                break;
            case 2:
                r = y;
                g = v;
                b = x;
                break;
            case 3:
                r = y;
                g = z;
                b = v;
                break;
            case 4:
                r = x;
                g = y;
                b = v;
                break;
            case 5:
                r = v;
                g = y;
                b = z;
                break;
            default:
                r = v;
                g = x;
                b = y;
                break;
        }
        red = (int) (MaxRGB * r + 0.5);
        green = (int) (MaxRGB * g + 0.5);
        blue = (int) (MaxRGB * b + 0.5);

        return Color.rgb(red,green,blue);
    }  //将hsl三个值转换为对应的rgb整型值得方法
}