package com.aihomework.voicedemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.aihomework.constants.Constants;

public class SumActivity extends Activity{
    Button confirmButton;
    TextView dataTextView;
    private int[] subjectOrders={0,0,0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sum_layout);

        Bundle bundle = this.getIntent().getExtras();
        String data = "";

        confirmButton = (Button)findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SumActivity.this, EntranceActivity.class);
                intent.putExtra(Constants.CONSTANT_SUBJECT, 0);
                startActivity(intent);
                System.exit(0);
            }
        });
        dataTextView = (TextView)findViewById(R.id.dataTextView);
        SharedPreferences sp = getSharedPreferences(Constants.SHARED_FILE_NAME, Activity.MODE_PRIVATE);
        StringBuilder res = new StringBuilder();
        res.append(sp.getString("chinese:\n"+Constants.SHARED_CN_WRONG_RADIX, "unfinished")+"\n");
        res.append(sp.getString("math:\n"+Constants.SHARED_MA_WRONG_RADIX, "unfinished")+"\n");
        res.append(sp.getString("english:\n"+Constants.SHARED_EN_WRONG_RADIX, "unfinished")+"\n");
        dataTextView.append("\n"+res);

    }

}
