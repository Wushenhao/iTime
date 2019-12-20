package com.example.itime.ui.timing;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.itime.ItimeMainActivity;
import com.example.itime.R;
import com.example.itime.TimeEditActivity;
import com.example.itime.model.Date;
import com.example.itime.model.FileDataSource;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.example.itime.ItimeMainActivity.REQUEST_CODE_NEW_TIME;
import static com.example.itime.ItimeMainActivity.REQUEST_CODE_UPDATE_TIME;
import static com.example.itime.TimeEditActivity.bitmap2Bytes;
import static com.example.itime.TimeEditActivity.drawableToBitamp;

public class TimingFragment extends Fragment implements Serializable{
    private TimingViewModel timingViewModel;
    private DateArrayAdapter theAdapter;
    private ArrayList<Date> theDates;
    private int theColors;
    private ListView listViewSuper;
    private FloatingActionButton fab;
    private AppBarLayout appbarLayout;
    private ImageView collapsingToolbarPic;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private DateFormat formattime =  DateFormat.getDateTimeInstance();
    private Calendar calendar = Calendar.getInstance(Locale.CHINA);
    private long timeStamp;
    private int count=1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        timingViewModel =
                ViewModelProviders.of(this).get(TimingViewModel.class);

        View root = inflater.inflate(R.layout.fragment_timing, container, false);
        final TextView textView = root.findViewById(R.id.text_timing);

        collapsingToolbarPic = this.getActivity().findViewById(R.id.collapsing_toolbar_pic);
        appbarLayout = this.getActivity().findViewById(R.id.appbar_layout);

        fab = this.getActivity().findViewById(R.id.fab);  //获得activityd的fab控件
        theDates = ((ItimeMainActivity)  getActivity()).theDate;
        theAdapter=new DateArrayAdapter(this.getActivity(),R.layout.item_time,theDates);
        textView1 = this.getActivity().findViewById(R.id.textView1);
        textView2 = this.getActivity().findViewById(R.id.textView2);
        textView3 = this.getActivity().findViewById(R.id.textView3);

        InitView(0);

        listViewSuper = (ListView)root.findViewById(R.id.list_view_timing);
        listViewSuper.setAdapter(theAdapter);
        setListViewHeightBasedOnChildren(listViewSuper);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), TimeEditActivity.class);
                theColors = ((ItimeMainActivity) getActivity()).theColor;  //获得主题色
                intent.putExtra("color", theColors);//传输主题颜色
                intent.putExtra("position", -1);
                startActivityForResult(intent, REQUEST_CODE_NEW_TIME);
            }
        });

        collapsingToolbarPic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                InitView(count);
                count = (count+1) % listViewSuper.getCount();
            }
        });

        listViewSuper.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getActivity(), TimeEditActivity.class);
                theColors = ((ItimeMainActivity) getActivity()).theColor;  //获得主题色
                intent.putExtra("color", theColors);//传输主题颜色
                intent.putExtra("position", position);
                intent.putExtra("time_title", theDates.get(position).getName());
                intent.putExtra("time_addition", theDates.get(position).getMessage());
                intent.putExtra("time_date", theDates.get(position).getDetail());
                intent.putExtra("time_picture", theDates.get(position).getCover());

                startActivityForResult(intent, REQUEST_CODE_UPDATE_TIME);
            }
        });

        listViewSuper.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick (AdapterView<?> parent, View view, int position, long id) {
                final int itemposition=position;
                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle("询问")
                        .setMessage("你确定要删除这一项吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                theDates.remove(itemposition);
                                theAdapter.notifyDataSetChanged(); //通知adapter底层数据已改变，修改数据
                                InitView(0);

                                Toast.makeText(getActivity(), "删除成功！", Toast.LENGTH_SHORT).show();
                            }
                        })   //点击确定来删除
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //取消则不做任何操作
                            }
                        })
                        .create().show();

                return true;
            }
        });
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
            final TextView todate = (TextView)date.findViewById(R.id.toDate);
            DateFormat formattime =  DateFormat.getDateTimeInstance();
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            CountDownTimer timer;
            long timeStamp;

            Date date_item = this.getItem(position);
            Drawable pic = new BitmapDrawable(BitmapFactory.decodeByteArray(date_item.getCover(),0,date_item.getCover().length)); //byte转换为drawable

            java.util.Date time = null;
            try {
                time = formattime.parse(date_item.getDetail());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (time != null){
                calendar.setTime(time);
                timeStamp = calendar.getTimeInMillis() - System.currentTimeMillis();
                timer = new CountDownTimer(timeStamp,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long day = millisUntilFinished / (1000 * 60 * 60 * 24);//天
                        todate.setText("只剩" + day + "天");
                    }
                    @Override
                    public void onFinish() {
                        todate.setText("已经历");
                    }
                };
                timer.start();
            }

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
                    int position = data.getIntExtra("position",0);
                    String title = data.getStringExtra("time_title");
                    String addition = data.getStringExtra("time_addition");
                    String date = data.getStringExtra("time_date");
                    byte[] itempic = data.getByteArrayExtra("time_picture");

                    if (itempic == null)
                        itempic = bitmap2Bytes(drawableToBitamp(ContextCompat.getDrawable(this.getActivity(), R.drawable.clock)));  //如果没有设置新建的图片，获取默认图片
                    theDates.add(new Date(title,date,addition,itempic)); //listViewSuper.getCount(),
                    theAdapter.notifyDataSetChanged();

                    setListViewHeightBasedOnChildren(listViewSuper);  //从TimeEditActivity返回没有重新创建实例，因此要再设置一次listview高度

                    InitView(0);

                    Toast.makeText(getActivity(), "新建成功", Toast.LENGTH_SHORT).show();
                    break;
                }

            case REQUEST_CODE_UPDATE_TIME:
                if (resultCode == RESULT_OK){
                    Bundle bundle= new Bundle();
                    int position = data.getIntExtra("position",0);
                    String title = data.getStringExtra("time_title");
                    String addition = data.getStringExtra("time_addition");
                    String date = data.getStringExtra("time_date");
                    byte[] itempic = data.getByteArrayExtra("time_picture");

                    if (date == null)
                        date = theDates.get(position).getDetail();
                    if (itempic == null)
                        itempic = theDates.get(position).getCover();  //如果没有更新时间和图片的话，不改变原来的图片和时间
                    theDates.remove(position);
                    theDates.add(position,new Date(title,date,addition,itempic)); //listViewSuper.getCount(),

                    theAdapter.notifyDataSetChanged();

                    InitView(0);

                    Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                    break;
                }
        }
    }   //从EditActivty返回后的操作

    private void InitView(int i){
        if (null != theDates && theDates.size() !=i) {
            Drawable pic = new BitmapDrawable(BitmapFactory.decodeByteArray(theDates.get(i).getCover(),0,theDates.get(i).getCover().length));
            collapsingToolbarPic.setImageDrawable(pic);
            textView1.setText(theDates.get(i).getName());
            textView2.setText(theDates.get(i).getDetail());

            java.util.Date time = null;
            try {
                time = formattime.parse(theDates.get(i).getDetail());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (time != null){
                calendar.setTime(time);
                timeStamp = calendar.getTimeInMillis() - System.currentTimeMillis();
                if (((ItimeMainActivity)  getActivity()).Timer != null)
                    ((ItimeMainActivity)  getActivity()).Timer .cancel();
                ((ItimeMainActivity)  getActivity()).Timer = new CountDownTimer(timeStamp,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long day = millisUntilFinished / (1000 * 60 * 60 * 24);//天
                        long hour = (millisUntilFinished - day * (1000 * 60 * 60 * 24)) / (1000 * 60 *60);  //时
                        long minute = (millisUntilFinished - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60)) / (1000 * 60);   //分
                        long second = (millisUntilFinished - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000;  //秒
                        textView3.setText(day + "天" + hour + "时" + minute + "分" + second + "秒");
                    }
                    @Override
                    public void onFinish() {
                        textView3.setText("已经历");
                    }
                };
                ((ItimeMainActivity)  getActivity()).Timer.start();
            }

            appbarLayout.setExpanded(true);
        }
        else {
            collapsingToolbarPic.setImageDrawable(ContextCompat.getDrawable(this.getActivity(), R.drawable.clock));
            ((ItimeMainActivity)  getActivity()).Timer.cancel();
            textView1.setText(null);
            textView2.setText(null);
            textView3.setText(null);
            appbarLayout.setExpanded(false);
        }  //判断是否有时间表项，有则展开并设置沉浸式任务栏图片，无则折叠
    }

}