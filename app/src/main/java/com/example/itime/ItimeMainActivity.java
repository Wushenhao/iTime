package com.example.itime;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import com.example.itime.model.Date;
import com.example.itime.ui.timing.TimingFragment;
import com.example.itime.ui.timing.TimingViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItimeMainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_NEW_TIME= 900;
    private AppBarConfiguration mAppBarConfiguration;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private DrawerLayout drawerlayout;
    private DateArrayAdapter theAdapter;
    private ArrayList<Date> theDates;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(android.R.id.content).setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        setContentView(R.layout.activity_main);

        theDates = new ArrayList<Date>();
        theDates.add(new Date("日期","长按使用日期计算器","长按使用日期计算器",R.drawable.ic_setting_date));
        theAdapter=new DateArrayAdapter(this,R.layout.item_time,theDates);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent)); //设置CollapsingToolbarLayout展开时渐变透明
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white)) ; //设置CollapsingToolbarLayout折叠后标题的颜色
        collapsingToolbarLayout.setBackgroundColor(getResources().getColor(R.color.colorBackGround));

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerlayout= (DrawerLayout) findViewById(R.id.drawer_layout);

        // Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ItimeMainActivity.this,TimeEditActivity.class);
                intent.putExtra("color", fab.getSolidColor());//传输主题颜色
                startActivityForResult(intent, REQUEST_CODE_NEW_TIME);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        /*
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        */
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_timing, R.id.nav_tag, R.id.nav_parts, R.id.nav_colors,
                R.id.nav_setting, R.id.nav_info)
                .setDrawerLayout(drawerlayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        final TimingViewModel timingViewModel = ViewModelProviders.of(this).get(TimingViewModel.class);
        timingViewModel.getAdapter().getValue(theAdapter);
        /*
        Bundle bundle = new Bundle();
        bundle.putSerializable("theAdapter",theAdapter);
        Navigation.findNavController(this, R.id.nav_host_fragment).restoreState(bundle); //传输listview的adapter给fragment
        */

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        ActionBarDrawerToggle actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerlayout,toolbar,R.string.nav_header_title,R.string.nav_header_title);
        actionBarDrawerToggle.syncState();
        drawerlayout.addDrawerListener(actionBarDrawerToggle);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode) {
            case REQUEST_CODE_NEW_TIME:
                if (resultCode == RESULT_OK) {
                    String title = data.getStringExtra("time_title");
                    String addition = data.getStringExtra("time_addition");
                    String date = data.getStringExtra("time_date");

                    //theBooks.add(position+1, new Book(title,addition,date,R.drawable.new_book));   //在当前位置下一位插入
                    //theAdapter.notifyDataSetChanged(); //通知adapter底层数据已改变，修改数据
                    Toast.makeText(this, "新建成功", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }   //从EditActivty返回后的操作

    public void changeThemeColor(int color){
        collapsingToolbarLayout.setContentScrimColor(color);
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
    } //修改主题颜色

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
            img.setImageResource(date_item.getCoverResourceId());
            name.setText(date_item.getName());
            detail.setText(date_item.getDetail());
            msg.setText(date_item.getMessage());
            return date;
        }
    }
}
