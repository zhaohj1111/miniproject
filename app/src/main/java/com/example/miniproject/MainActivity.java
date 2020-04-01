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
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity {

    private String txt1;
    private String txt2;
    private ArrayList list=new ArrayList();
    private ArrayList list_detail=new ArrayList();

//    private ArrayList list_eng=new ArrayList();
//    private ArrayList list_detail_eng=new ArrayList();

    private ListView listView;
    private MyAdapter myAdapter;

    //private TextView newText;
    private TextView listNewsTitle;
    private TextView listNewsContent;

    static String to;//目标译文 可变 zh中文 en英文
//    private String eng_result;

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
            listNewsContent=view.findViewById(R.id.item_text);
            listNewsContent.setText("position:"+position);
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
                        Log.e("TAG","Jsoup ======>>" + txt1);

                    }
                }catch (IOException e){
                    e.printStackTrace();;
                }
            }
        });
    }

    //链接百度API
    public interface BaiduTranslateService {
        //翻译接口
        //表示提交表单数据，@Field注解键名
        //适用于数据量少的情况
        @POST("translate")
        @FormUrlEncoded
        Call<test> translate(@Field("q") String q, @Field("from") String from, @Field("to") String to, @Field("appid") String appid, @Field("salt") String salt,
                             @Field("sign") String sign);
    }

    public static class MD5Utils {

        /**
         * MD5加密算法使用 对字符串加密
         *
         * @param info 参数为需要加密的String
         * @return 返回加密后的String
         */
        public static String getMD5Code(String info) {
            try {
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                md5.update(info.getBytes("utf-8"));//设置编码格式
                byte[] encryption = md5.digest();
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < encryption.length; i++) {
                    if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                        stringBuffer.append("0").append(Integer.toHexString(0xff & encryption[i]));
                    } else {
                        stringBuffer.append(Integer.toHexString(0xff & encryption[i]));
                    }
                }
                return stringBuffer.toString();
            } catch (Exception e) {
                return "MD5加密异常";
            }
        }

    }

    public void translate(String word, final View view){
        String from = "auto";//源语种 en 英语 zh 中文

        if (word.length() == word.getBytes().length) {//成立则说明没有汉字，否则由汉字。
            to = "zh"; //没有汉字 英译中
        } else {
            to = "en";//含有汉字 中译英
        }
        String appid = "20200331000409514";//appid 管理控制台有
        String salt = (int) (Math.random() * 100 + 1) + "";//随机数 这里范围是[0,100]整数 无强制要求
        String key = "sY2vk9aJTl0WAKuwjnn7";//密钥 管理控制台有
        String string1 = appid + word + salt + key;// string1 = appid+q+salt+密钥
        String sign = MD5Utils.getMD5Code(string1);// 签名 = string1的MD5加密 32位字母小写
        Log.i("fanyi", "string1：" + string1);
        Log.i("fanyi", "sign: " + sign);

        Retrofit retrofitBaidu = new Retrofit.Builder()
                .baseUrl("https://fanyi-api.baidu.com/api/trans/vip/")
                .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                .build();
        BaiduTranslateService baiduTranslateService = retrofitBaidu.create(BaiduTranslateService.class);


        Call<test> call = baiduTranslateService.translate(word, from, to, appid, salt, sign);
        call.enqueue(new Callback<test>() {
            @Override
            public void onResponse(Call<test> call, Response<test> response) {
                //请求成功
                Log.i("fanyi", "onResponse: 请求成功");
                test respondBean = response.body();//返回的JSON字符串对应的对象
                String result = respondBean.getTrans_result().get(0).getDst();//获取翻译的字符串String
                Log.i("fanyi", "英译中结果" + result);
                //try code below this.
            }

            @Override
            public void onFailure(Call<test> call, Throwable t) {
                //请求失败 打印异常
                Log.i("fanyi", "onResponse: 请求失败 " + t);
            }
        });
    }


//    public void makeTranslateArray(){
//
//        for(int i=0;i<list.size();i++)
//        {
//            translate((String)list.get(i),list_eng);
//        }
//    }
}

