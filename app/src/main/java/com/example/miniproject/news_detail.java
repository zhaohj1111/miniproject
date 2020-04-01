package com.example.miniproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.security.MessageDigest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class news_detail extends AppCompatActivity {

    private TextView txt_title;
    private TextView txt_detail;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    static String to;//目标译文 可变 zh中文 en英文

    String txt1;
    String txt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        txt_title=findViewById(R.id.news_title);
        txt_detail=findViewById(R.id.news_details);
        btn1=findViewById(R.id.bot_btn1);
        btn2=findViewById(R.id.bot_btn2);
        btn3=findViewById(R.id.bot_btn3);
        Intent intent=getIntent();
        txt1=intent.getStringExtra("title");
        txt2=intent.getStringExtra("detail");
        txt_title.setText(txt1);
        txt_detail.setText(txt2);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate_tilte(txt1);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translate(txt2);
                //translate(txt2);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    public void translate(String word){
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
        String sign = MainActivity.MD5Utils.getMD5Code(string1);// 签名 = string1的MD5加密 32位字母小写
        Log.i("fanyi", "string1：" + string1);
        Log.i("fanyi", "sign: " + sign);

        Retrofit retrofitBaidu = new Retrofit.Builder()
                .baseUrl("https://fanyi-api.baidu.com/api/trans/vip/")
                .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                .build();
        MainActivity.BaiduTranslateService baiduTranslateService = retrofitBaidu.create(MainActivity.BaiduTranslateService.class);


        Call<test> call = baiduTranslateService.translate(word, from, to, appid, salt, sign);
        call.enqueue(new Callback<test>() {
            @Override
            public void onResponse(Call<test> call, Response<test> response) {
                //请求成功
                Log.i("fanyi", "onResponse: 请求成功");
                test respondBean = response.body();//返回的JSON字符串对应的对象
                String result = respondBean.getTrans_result().get(0).getDst();//获取翻译的字符串String
                Log.i("fanyi", "英译中结果" + result);
                txt_detail.setText(result);
                //inthistoChange.
            }

            @Override
            public void onFailure(Call<test> call, Throwable t) {
                //请求失败 打印异常
                Log.i("fanyi", "onResponse: 请求失败 " + t);
            }
        });
    }

    public void translate_tilte(String word){
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
        String sign = MainActivity.MD5Utils.getMD5Code(string1);// 签名 = string1的MD5加密 32位字母小写
        Log.i("fanyi", "string1：" + string1);
        Log.i("fanyi", "sign: " + sign);

        Retrofit retrofitBaidu = new Retrofit.Builder()
                .baseUrl("https://fanyi-api.baidu.com/api/trans/vip/")
                .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                .build();
        MainActivity.BaiduTranslateService baiduTranslateService = retrofitBaidu.create(MainActivity.BaiduTranslateService.class);


        Call<test> call = baiduTranslateService.translate(word, from, to, appid, salt, sign);
        call.enqueue(new Callback<test>() {
            @Override
            public void onResponse(Call<test> call, Response<test> response) {
                //请求成功
                Log.i("fanyi", "onResponse: 请求成功");
                test respondBean = response.body();//返回的JSON字符串对应的对象
                String result = respondBean.getTrans_result().get(0).getDst();//获取翻译的字符串String
                Log.i("fanyi", "英译中结果" + result);
                txt_title.setText(result);
                //inthistoChange.
            }

            @Override
            public void onFailure(Call<test> call, Throwable t) {
                //请求失败 打印异常
                Log.i("fanyi", "onResponse: 请求失败 " + t);
            }
        });
    }

}
