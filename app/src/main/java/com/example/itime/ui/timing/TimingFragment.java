package com.example.itime.ui.timing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.itime.ItimeMainActivity;
import com.example.itime.R;
import com.example.itime.TimeEditActivity;
import com.example.itime.model.Date;
import com.example.itime.model.FileDataSource;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.example.itime.ItimeMainActivity.REQUEST_CODE_NEW_TIME;

public class TimingFragment extends Fragment implements Serializable{
    private TimingViewModel timingViewModel;
    private DateArrayAdapter theAdapter;
    private ArrayList<Date> theDates;
    private ListView listViewSuper;
    private FloatingActionButton fab;
    private ImageView collapsingToolbarPic;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        timingViewModel =
                ViewModelProviders.of(this).get(TimingViewModel.class);

        View root = inflater.inflate(R.layout.fragment_timing, container, false);
        final TextView textView = root.findViewById(R.id.text_timing);

        collapsingToolbarPic = this.getActivity().findViewById(R.id.collapsing_toolbar_pic);

        fab = this.getActivity().findViewById(R.id.fab);  //获得activityd的fab控件
        theDates = ((ItimeMainActivity)  getActivity()).theDate;

        //theDates.add(new Date("日期","长按使用日期计算器","长按使用日期计算器",R.drawable.hamster));
        theAdapter=new DateArrayAdapter(this.getActivity(),R.layout.item_time,theDates);

        if (null != theDates && theDates.size() !=0) {
            Drawable pic = new BitmapDrawable(BitmapFactory.decodeByteArray(theDates.get(0).getCover(),0,theDates.get(0).getCover().length));
            collapsingToolbarPic.setImageDrawable(pic);
        }

        listViewSuper = (ListView)root.findViewById(R.id.list_view_timing);
        listViewSuper.setAdapter(theAdapter);
        setListViewHeightBasedOnChildren(listViewSuper);
        //collapsingToolbarPic.setBackgroundColor(getResources().getColor(R.color.colorBackGround));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), TimeEditActivity.class);
                intent.putExtra("color", fab.getSolidColor());//传输主题颜色
                startActivityForResult(intent, REQUEST_CODE_NEW_TIME);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
        /*
        timingViewModel.getAdapter().observe(this, new Observer<ItimeMainActivity.DateArrayAdapter>() {
            @Override
            public void onChanged(@Nullable ItimeMainActivity.DateArrayAdapter adapter) {
                dateArrayAdapter = adapter;
            }
        });
        */
        return root;
    }

    public class DateArrayAdapter extends ArrayAdapter<Date> implements Serializable
    {
        private int resourceid;

        public DateArrayAdapter(@NonNull Context context, int resource, @NonNull List<Date> objects) {
            super(context, resource, objects);
            this.resourceid = resource;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater= LayoutInflater.from(this.getContext());
            View date = mInflater.inflate(this.resourceid,null);

            ImageView img = (ImageView)date.findViewById(R.id.img_picture);
            TextView name = (TextView)date.findViewById(R.id.date_name);
            TextView detail = (TextView)date.findViewById(R.id.date_detail);
            TextView msg = (TextView)date.findViewById(R.id.date_msg);

            Date date_item = this.getItem(position);
            Drawable pic = new BitmapDrawable(BitmapFactory.decodeByteArray(date_item.getCover(),0,date_item.getCover().length)); //byte转换为drawable

            img.setImageDrawable(pic);
            name.setText(date_item.getName());
            detail.setText(date_item.getDetail());
            msg.setText(date_item.getMessage());
            return date;
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter(); if (listAdapter == null) { // pre-condition
            return;
        }
        int totalHeight = 0; for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView); // listItem.measure(0, 0);
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    } //动态设置listview高度，使得ScrollView能正常显示足够多行数

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode) {
            case REQUEST_CODE_NEW_TIME:
                if (resultCode == RESULT_OK) {
                    Bundle bundle= new Bundle();
                    String title = data.getStringExtra("time_title");
                    String addition = data.getStringExtra("time_addition");
                    String date = data.getStringExtra("time_date");
                    byte[] itempic = data.getByteArrayExtra("time_picture");

                    theDates.add(new Date(title,date,addition,itempic)); //listViewSuper.getCount(),

                    //theBooks.add(position+1, new Book(title,addition,date,R.drawable.new_book));   //在当前位置下一位插入
                    //theAdapter.notifyDataSetChanged(); //通知adapter底层数据已改变，修改数据
                    theAdapter.notifyDataSetChanged();

                    if (null != theDates && theDates.size() !=0) {
                        Drawable pic = new BitmapDrawable(BitmapFactory.decodeByteArray(theDates.get(0).getCover(),0,theDates.get(0).getCover().length));
                        collapsingToolbarPic.setImageDrawable(pic);
                    }

                    Toast.makeText(getActivity(), "新建成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }   //从EditActivty返回后的操作

}