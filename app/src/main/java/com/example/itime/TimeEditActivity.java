package com.example.itime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class TimeEditActivity extends AppCompatActivity {

    private EditText editTimeTitle,editTimeAddition,editTimeDate;
    private Button buttonOk,buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_edit);


        editTimeTitle=(EditText)findViewById(R.id.edit_title_content);
        editTimeAddition=(EditText)findViewById(R.id.edit_addition_content);
        editTimeDate=(EditText)findViewById(R.id.edit_date);

        buttonCancel=(Button)findViewById(R.id.button_cancel);
        buttonOk=(Button)findViewById(R.id.button_ok);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("time_title", editTimeTitle.getText().toString().trim());
                intent.putExtra("time_addition", editTimeAddition.getText().toString().trim());
                intent.putExtra("time_date", editTimeDate.getText().toString().trim());

                setResult(RESULT_OK, intent);
                TimeEditActivity.this.finish();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeEditActivity.this.finish();
            }
        });
    }
}
