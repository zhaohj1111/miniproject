package com.example.miniproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class news_detail extends AppCompatActivity {

    private TextView txt_title;
    private TextView txt_detail;
    private Button btn3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        txt_title=findViewById(R.id.news_title);
        txt_detail=findViewById(R.id.news_details);
        btn3=findViewById(R.id.bot_btn3);
        Intent intent=getIntent();
        String txt1;
        String txt2;
        txt1=intent.getStringExtra("title");
        txt2=intent.getStringExtra("detail");
        txt_title.setText(txt1);
        txt_detail.setText(txt2);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
