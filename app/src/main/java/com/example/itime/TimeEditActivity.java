package com.example.itime;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.example.itime.model.Setting;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.media.MediaRecorder.VideoSource.CAMERA;

public class TimeEditActivity extends AppCompatActivity implements Serializable {

    public static final int PHOTO_REQUEST_GALLERY = 901;
    public static final String PHOTO_FILE_NAME = "default_image.jpg";
    public static final int PHOTO_REQUEST_GALLERY2 = 902;
    public static final int PHOTO_REQUEST_CAMERA = 903;
    public static final int PHOTO_REQUEST_CUT =904;
    private EditText editTimeTitle,editTimeAddition;
    private String editTimeDate;  //用于计算距离设定日期的倒计时
    private Button buttonOk,buttonCancel;
    private SettingArrayAdapter theAdapter;
    private ArrayList<Setting> theSettings;
    private File tempFile;
    private Uri filepath;
    private RelativeLayout temp;    //当前背景容器的layout
    private Context context;

    private int dateClick = 0;
    private int pictureClick = 0;
    private TextView count_time;
    private CountDownTimer timer;
    private long timeStamp = 86400000;

    private ListView item_date;
    DateFormat formattime =  DateFormat.getDateTimeInstance();
    //获取日期格式器对象
    Calendar calendar = Calendar.getInstance(Locale.CHINA);
    //获取日期格式器对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_edit);
        context=this;

        //实现沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.getWindow().setStatusBarColor(Color.TRANSPARENT);
            this.getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        temp=(RelativeLayout)findViewById(R.id.edit_relativelayout);
        final int position= getIntent().getIntExtra("position", 0);
        int color= getIntent().getIntExtra("color", Color.rgb(0,150,136));
        changeSettingColor(color);

        InitData();

        theAdapter=new SettingArrayAdapter(this,R.layout.item_time_set,theSettings);
        ListView listViewSuper = (ListView) findViewById(R.id.list_view_setting);
        listViewSuper.setAdapter(theAdapter);

        count_time = (TextView) findViewById(R.id.time);
        editTimeTitle=(EditText)findViewById(R.id.edit_title_content);
        editTimeAddition=(EditText)findViewById(R.id.edit_addition_content);

        if (position != -1){
            String title= getIntent().getStringExtra("time_title");
            String addition= getIntent().getStringExtra("time_addition");
            String date= getIntent().getStringExtra("time_date");
            byte[] picture= getIntent().getByteArrayExtra("time_picture");
            Drawable pic = new BitmapDrawable(BitmapFactory.decodeByteArray(picture,0,picture.length)); //byte转换为drawable

            editTimeTitle.setText(title);
            editTimeAddition.setText(addition);
            theSettings.get(0).setMessage(date);
            temp.setBackground(pic);
        }

        buttonCancel=(Button)findViewById(R.id.button_cancel);
        buttonOk=(Button)findViewById(R.id.button_ok);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String test = "长按使用日期计算器";
                if ( !theSettings.get(0).getMessage().equals(test)){
                    Intent intent = new Intent();
                    byte[] itempic=null;
                    if (!(temp.getBackground() instanceof ColorDrawable))  //判断是否设置图片，不是则itempic为空
                        itempic = bitmap2Bytes(drawableToBitamp(temp.getBackground())); //二进制数组图片

                    intent.putExtra("position", position);
                    intent.putExtra("time_title", editTimeTitle.getText().toString().trim());
                    intent.putExtra("time_addition", editTimeAddition.getText().toString().trim());
                    if (dateClick != 0)
                        intent.putExtra("time_date", editTimeDate);
                    if (pictureClick != 0)
                        intent.putExtra("time_picture", itempic);

                    setResult(RESULT_OK, intent);
                    TimeEditActivity.this.finish();
                }
                else Toast.makeText(TimeEditActivity.this,"时间未选择", Toast.LENGTH_SHORT).show();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeEditActivity.this.finish();
            }
        });


        //此处设置ListView的点击事件
        item_date = findViewById(R.id.list_view_setting);
        //得到页面设定日期的按钮控件对象
        item_date.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //设置按钮的点击事件监听器

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    //生成一个DatePickerDialog对象，显示的DatePickerDialog控件可以选择年月日，并设置
                    final DatePickerDialog datePickerDialog = new DatePickerDialog(TimeEditActivity.this, new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            //由于使用了按钮，不在此处回调
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                    datePickerDialog.setCancelable(true);
                    datePickerDialog.setCanceledOnTouchOutside(true);
                    datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //生成一个TimePickerDialog对象，显示的TimePickerDialog控件可以选择年月日，并设置
                            final TimePickerDialog timePickerDialog = new TimePickerDialog(TimeEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    //此处修改日历控件的日期、时与分，之后修改item的内容
                                    //同DatePickerDialog控件，此处实现所有的回调操作
                                    calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                    calendar.set(Calendar.MINUTE,minute);
                                    calendar.set(Calendar.SECOND,0);

                                    calendar.set(Calendar.YEAR, datePickerDialog.getDatePicker().getYear());
                                    calendar.set(Calendar.MONTH, datePickerDialog.getDatePicker().getMonth());
                                    calendar.set(Calendar.DAY_OF_MONTH, datePickerDialog.getDatePicker().getDayOfMonth());

                                    Setting settingAtPosition=theSettings.get(0);   //新建Setting对象，复制为当前的位置的ArrayList<Setting>的Setting对象
                                    editTimeDate = formattime.format(calendar.getTime());
                                    timeStamp = calendar.getTimeInMillis() - System.currentTimeMillis();

                                    settingAtPosition.setMessage(formattime.format(calendar.getTime()));
                                    theAdapter.notifyDataSetChanged(); //通知adapter底层数据已改变，修改数据
                                    //将页面TextView的显示更新为最新时间

                                    if (timer != null)
                                        timer.cancel();
                                    timer = new CountDownTimer(timeStamp,1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            long day = millisUntilFinished / (1000 * 60 * 60 * 24);//天
                                            long hour = (millisUntilFinished - day * (1000 * 60 * 60 * 24)) / (1000 * 60 *60);  //时
                                            long minute = (millisUntilFinished - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60)) / (1000 * 60);   //分
                                            long second = (millisUntilFinished - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000;  //秒
                                            count_time.setText(day + "天" + hour + "时 " + minute + "分 " + second + "秒 ");
                                        }

                                        @Override
                                        public void onFinish() {

                                        }
                                    };
                                    timer.start();
                                    dateClick++;
                                 }
                             },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true);

                            //此处显示时间弹窗，修改日历控件的日期，之后修改item的内容，在此处作为按钮响应实现所有的回调操作
                            timePickerDialog.show(); //显示时间弹窗

                            calendar.set(Calendar.HOUR_OF_DAY,0);
                            calendar.set(Calendar.MINUTE,0);
                            calendar.set(Calendar.SECOND,0); //只选择日期则时分秒皆为0

                            calendar.set(Calendar.YEAR, datePickerDialog.getDatePicker().getYear());
                            calendar.set(Calendar.MONTH, datePickerDialog.getDatePicker().getMonth());
                            calendar.set(Calendar.DAY_OF_MONTH, datePickerDialog.getDatePicker().getDayOfMonth());

                            Setting settingAtPosition=theSettings.get(0);   //新建Setting对象，复制为当前的位置的ArrayList<Setting>的Setting对象
                            editTimeDate = formattime.format(calendar.getTime());
                            timeStamp = calendar.getTimeInMillis() - System.currentTimeMillis();

                            settingAtPosition.setMessage(formattime.format(calendar.getTime()));
                            theAdapter.notifyDataSetChanged(); //通知adapter底层数据已改变，修改数据

                            if (timer != null)
                                timer.cancel();
                            timer = new CountDownTimer(timeStamp,1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    long day = millisUntilFinished / (1000 * 60 * 60 * 24);//天
                                    long hour = (millisUntilFinished - day * (1000 * 60 * 60 * 24)) / (1000 * 60 *60);  //时
                                    long minute = (millisUntilFinished - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60)) / (1000 * 60);   //分
                                    long second = (millisUntilFinished - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000;  //秒
                                    count_time.setText(day + "天" + hour + "时 " + minute + "分 " + second + "秒 ");
                                }

                                @Override
                                public void onFinish() {
                                    count_time.setText("已经历");
                                }
                            };
                            timer.start();
                            dateClick++;
                        }
                    });

                    datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

                    datePickerDialog.show();  //显示日历时间弹窗
                    //将页面ListView中第一个item的TextView的显示更新为最新时间
                } //设置时间

                else if (position == 2){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("选择图片");
                    builder.setMessage("给新建的提醒时间设置醒目的图片吧！");
                    builder.setPositiveButton("图库", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            gallery();
                        }
                    });
                    builder.setNegativeButton("拍照", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            camera();
                        }
                    });
                    builder.create();//创建
                    builder.show();//显示
                }  //设置图片

            }
        });

        Date date = null;
        try {
            date = formattime.parse(theSettings.get(0).getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null){
            calendar.setTime(date);
            timeStamp = calendar.getTimeInMillis() - System.currentTimeMillis();
            if (timer != null)
                timer.cancel();
            timer = new CountDownTimer(timeStamp,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long day = millisUntilFinished / (1000 * 60 * 60 * 24);//天
                    long hour = (millisUntilFinished - day * (1000 * 60 * 60 * 24)) / (1000 * 60 *60);  //时
                    long minute = (millisUntilFinished - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60)) / (1000 * 60);   //分
                    long second = (millisUntilFinished - day * (1000 * 60 * 60 * 24) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000;  //秒
                    count_time.setText(day + "天" + hour + "时 " + minute + "分 " + second + "秒 ");
                }

                @Override
                public void onFinish() {

                }
            };
            timer.start();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_REQUEST_GALLERY2) {
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                String path = getAbsoluteImagePath(context,uri);
                filepath=Uri.parse("file://"+path);
                crop(Uri.parse("file://"+path));//调用剪贴图片代码
            }

        }  else if (requestCode == PHOTO_REQUEST_GALLERY) {
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                String path = getAbsoluteImagePath(context,uri);
                filepath=Uri.parse("file://"+path);
                Log.e("uri", uri.toString());
                Log.e("filepath", filepath.toString());

                crop(Uri.parse("file://"+path));//调用剪贴图片代码
            }

        }else if (requestCode == PHOTO_REQUEST_CAMERA) {
            crop(Uri.fromFile(tempFile)); //调用剪贴图片代码

        }else if (requestCode == PHOTO_REQUEST_CUT) {
            try {

                Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(Uri.fromFile(tempFile)));
                BitmapDrawable picture = new BitmapDrawable(context.getResources(),bitmap);
                Log.e("uri", Uri.fromFile(tempFile).toString());

                temp.setBackground(picture);
                pictureClick++;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }   //通过返回值设置照片

    private void InitData(){
        theSettings = new ArrayList<Setting>();
        theSettings.add(new Setting("日期","长按使用日期计算器",R.drawable.ic_setting_date));
        theSettings.add(new Setting("重复设置","无",R.drawable.ic_setting_loop));
        theSettings.add(new Setting("图片","",R.drawable.ic_setting_picture));
        theSettings.add(new Setting("添加标签","",R.drawable.ic_setting_label));
    }

    protected class SettingArrayAdapter extends ArrayAdapter<Setting>
    {
        private int resourceid;

        public SettingArrayAdapter(@NonNull Context context, int resource, @NonNull List<Setting> objects) {
            super(context, resource, objects);
            this.resourceid = resource;
        }

        @NonNull
        @Override

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater= LayoutInflater.from(this.getContext());
            View setting = mInflater.inflate(this.resourceid,null);

            ImageView img = (ImageView)setting.findViewById(R.id.img_avatar);
            TextView name = (TextView)setting.findViewById(R.id.set_name);
            TextView msg = (TextView)setting.findViewById(R.id.set_msg);

            Setting setting_item = this.getItem(position);
            img.setImageResource(setting_item.getCoverResourceId());
            name.setText(setting_item.getTitle());
            msg.setText(setting_item.getMessage());
            return setting;
        }
    }

    protected static String getAbsoluteImagePath(Context context, Uri uri)
    {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider

                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())){
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }  //获得图片的绝对路径

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        final String column = "_data";
        final String[] projection = {column};

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(column);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public void gallery() {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT,ACTION_GET_CONTENT
        File imagePath = new File(Environment.getExternalStorageDirectory(), "Download");

        tempFile = new File(imagePath, PHOTO_FILE_NAME);
        if(tempFile.exists())
            Log.e("FILE", "File exist");

        if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.KITKAT) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1); //申请读取权限

            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", tempFile);
            intent.setDataAndType(contentUri, "*/*");

            startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1); //申请读取权限

            intent.setDataAndType(Uri.fromFile(tempFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(intent, PHOTO_REQUEST_GALLERY2);
        }
    }

    public void camera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        if(android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.KITKAT) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA);  //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment.getDataDirectory(), PHOTO_FILE_NAME)));
        }
        startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
    }

    private void crop(Uri uri) {
        Log.e("URI", uri.getPath());
        Log.e("URI", uri.toString());
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");

        File cutfile = new File(Environment.getDataDirectory(), "selectedpic.png");
        if (cutfile.exists()){
            cutfile.delete();
        }

        //申请线程读取权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 412);
        intent.putExtra("aspectY", 270);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 412);
        intent.putExtra("outputY", 270);
        intent.putExtra("scale", true);//黑边
        intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(tempFile));  //Uri.fromFile(tempFile)
        Log.e("tempFile", Uri.fromFile(tempFile).toString());
        // 图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri

        startActivityForResult(intent, PHOTO_REQUEST_CUT);

    }  //对图片的剪切操作

    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }  //判断储存卡是否可用

    public static Bitmap drawableToBitamp(Drawable drawable) {
        Bitmap bitmap;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        System.out.println("Drawable转Bitmap");
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    } //drawable转化成bitmap的方法

    public static byte[] bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }  //bitmap转化成byte数组

    private void changeSettingColor(int color) {
        temp.setBackgroundColor(color);
    }
}