package com.example.itime;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;

import com.example.itime.model.Date;
import com.example.itime.model.FileDataSource;
import com.example.itime.ui.timing.TimingFragment;
import com.example.itime.ui.timing.TimingViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.CountDownTimer;
import android.util.Log;
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
    public static final int REQUEST_CODE_UPDATE_TIME= 910;
    public ArrayList<Date> theDate;
    public int theColor;
    public CountDownTimer Timer;
    private FileDataSource fileDataSource;
    private AppBarConfiguration mAppBarConfiguration;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton fab;
    private NavigationView navigationView;
    private DrawerLayout drawerlayout;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(android.R.id.content).setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        theDate = new ArrayList<Date>();

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent)); //设置CollapsingToolbarLayout展开时渐变透明
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(android.R.color.white)) ; //设置CollapsingToolbarLayout折叠后标题的颜色
        collapsingToolbarLayout.setBackgroundColor(getResources().getColor(R.color.colorBackGround));

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerlayout= (DrawerLayout) findViewById(R.id.drawer_layout);

        // Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = findViewById(R.id.fab);

        InitData();   //初始化主题色和listview

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_timing, R.id.nav_tag, R.id.nav_parts, R.id.nav_colors,
                R.id.nav_setting, R.id.nav_info)
                .setDrawerLayout(drawerlayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
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

    public void changeThemeColor(int color){
        collapsingToolbarLayout.setContentScrimColor(color);
        fab.setBackgroundTintList(ColorStateList.valueOf(color));
        theColor=color;
    } //修改主题颜色

    @Override
    public void onDestroy() {
        super.onDestroy();
        fileDataSource.save(theColor);
    } //程序被返回键销毁时，能够保存数据

    private void InitData(){
        fileDataSource=new FileDataSource(this);
        theDate=fileDataSource.load();
        theColor=fileDataSource.loadcolor();
        if (theDate.size()==0) {
        }

        if (theColor != 0){
            changeThemeColor(theColor);
        }
    }
}
