package com.example.miniproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String txt1;
    private String txt2;
    private ArrayList list=new ArrayList();
    private ArrayList list_detail=new ArrayList();

    private ListView listView;
    private MyAdapter myAdapter;

    //private TextView newText;
    private TextView listNewsTitle;
    private TextView listNewsContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //no strarting
        listView=findViewById(R.id.list_view);

        Thread t = getDataByJsoup();
        t.start();
        try {
            t.join();
            myAdapter=new MyAdapter();
            listView.setAdapter(myAdapter);
            //Log.i("mytag",(String)list.get(2));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for(int i=0;i<=list.size();i++){

                    if(i==position){
                        Intent intent0=new Intent();
                        intent0.setClass(MainActivity.this,news_detail.class);
                        intent0.putExtra("title",(String)list.get(position));
                        intent0.putExtra("detail",(String)list_detail.get(position));
                        startActivity(intent0);

                    }
                }
            }
        });

    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view=getLayoutInflater().inflate(R.layout.item,null);
            listNewsTitle=view.findViewById(R.id.item_title);
            listNewsTitle.setText((String)list.get(position));
            Log.i("test",(String)list.get(position));
            return view;
        }
    }

    private Thread getDataByJsoup(){

        // 开启一个新线程
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Document doc= Jsoup.connect("http://news.cqu.edu.cn/newsv2").get();
                    Elements elements=doc.select("div.rmditem");
                    //Log.i("mytag",elements.select("a").attr("title"));
                    for (Element element:elements){

                        txt1=element.select("a").attr("title");
                        txt2=element.select("p").text();
                        list.add(txt1);
                        list_detail.add(txt2);
                        Log.e("TAG","Jsoup ======>>" + txt1+txt2);
                    }
                }catch (IOException e){
                    e.printStackTrace();;
                }
            }
        });
    }
}
