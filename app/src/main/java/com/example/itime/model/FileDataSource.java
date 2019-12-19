package com.example.itime.model;

import android.content.Context;
import android.util.Log;

import com.example.itime.model.Date;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Created by jszx on 2019/10/14.
 */

public class FileDataSource {
    private Context context;

    public FileDataSource(Context context) {
        this.context = context;
    }

    public ArrayList<Date> getDates() {
        return dates;
    }

    private ArrayList<Date> dates=new ArrayList<Date>();
    private int Color;

    public void save(int color)
    {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(
                    context.openFileOutput("Serializable.txt",Context.MODE_PRIVATE)
            );
            outputStream.writeObject(dates);      //建立对象输出流
            outputStream.close();

            Color = color;
            ObjectOutputStream outputStream2 = new ObjectOutputStream(
                    context.openFileOutput("Serializable2.txt",Context.MODE_PRIVATE)
            );
            outputStream2.writeObject(Color);      //建立对象输出流
            outputStream2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Date> load()
    {
        try{
            ObjectInputStream inputStream = new ObjectInputStream(
                    context.openFileInput("Serializable.txt")
            );
            dates = (ArrayList<Date>) inputStream.readObject();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dates;
    }

    public int loadcolor()
    {
        try{
            ObjectInputStream inputStream = new ObjectInputStream(
                    context.openFileInput("Serializable2.txt")
            );
            Color = (int) inputStream.readObject();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Color;
    }
}
