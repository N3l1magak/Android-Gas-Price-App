package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inspector.StaticInspectionCompanionProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.HttpResponse;
import com.example.test.util.NetUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;




public class MainActivity extends AppCompatActivity implements View.OnClickListener { //implements View.OnClickListener{

    //    private TextView responseText; // 显示网络请求的结果
//    private EditText get_province; //输入那个城市，然后进行网络请求去寻找Json数据
//    private String strFromnet;
//    private Handler mhanlder=new Handler(Looper.myLooper()){
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            if(msg.what==0){
//                String strData =(String) msg.obj;
//                responseText.setText(strData);
//                Toast.makeText(MainActivity.this,"主线程收到来自网络的消息",Toast.LENGTH_LONG).show();
//            }
//        }
//    };
//
//    String province;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        // 获取所有的控件
//        Button sendRequest = (Button) findViewById(R.id.send_request);
//        responseText = (TextView) findViewById(R.id.response_text);
//        get_province=(EditText) findViewById(R.id.province);
//        province=get_province.getText().toString();
//
////        sendRequest.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                start(view);
////            }
////        });
//    }
//
//    public void start(View view){
//        //做一个耗时任务
//
//        //String strFromnet=getStringFromNet(); //无线程，模拟在主线程执行
//        Thread thread=new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                    strFromnet=getStringFromNet();  //
//
//
//                //responseText.setText(strFromnet);//子线程操作主线程的UI界面报错
//                Message msg=new Message();
//                msg.what=0; //区分谁发的消息
//                msg.obj=strFromnet;
//                mhanlder.sendMessage(msg);//利用主线程的handler给主线程发消息
//            }
//        });
//        thread.start(); //子线程开启
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
////                strFromnet=getStringFromNet(); //在子线程执行获取网络数据
////                responseText.setText(strFromnet);
////                try {
////                    Thread.sleep(1000);
////                }catch (InterruptedException e){
////                    e.printStackTrace();
////                }
////            }
////        }).start();
//
//       // responseText.setText(strFromnet);
//        Toast.makeText(this,"开启子线程请求网络",Toast.LENGTH_LONG).show();
//    }
//    private String getStringFromNet()  {
//        return NetUtil.GetgasofCity("北京");
////        //假装从网络获取字符串或者数据
////        String result="";
////        StringBuilder stringBuilder=new StringBuilder();
////
////        //模拟一个耗时的任务
////        for (int i=0;i<100;i++)
////        {
////            stringBuilder.append("字符串"+i);
////        }
////
////        try {
////            Thread.sleep(6*1000);
////        }
////        catch (InterruptedException e)
////        {
////            e.printStackTrace();
////        }
////        result=stringBuilder.toString();
////        return  result;
//
//    }
//}

        EditText enter_province;
        Button sendRequest;
        TextView responseText;
        private String province;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            //找到所有控件
            enter_province=(EditText)findViewById(R.id.enetr_province);
            sendRequest = (Button) findViewById(R.id.send_request);
            responseText = (TextView) findViewById(R.id.response_text);
            //设置输入框的值
            province=enter_province.getText().toString();
            //设置发送http请求按键的事件
            sendRequest.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) { //重写点击事件的函数
            if (v.getId() == R.id.send_request) {
                sendRequestWithOkHttp();
            }
        }

        private void sendRequestWithOkHttp() {
            // 开启线程来发起网络请求，子线程处理
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try {
                        OkHttpClient client = new OkHttpClient();


//                        // post请求
//                        RequestBody requestBody = new FormBody.Builder()
//                                .add("type","1")
//                                .build();


                        Request request = new Request.Builder()
//                                .url("https://api.jisuapi.com/oil/query?appkey=3f17253cd3cffbfc&province="+enter_province.getText().toString())
//                                .post(requestBody)
//                                .build();
                                .url("https://gas-price.p.rapidapi.com/stateUsaPrice?state=" + enter_province.getText().toString())
                                .get()
                                .addHeader("x-rapidapi-host", "gas-price.p.rapidapi.com")
                                .addHeader("x-rapidapi-key", "936940fd03msh5080e5060ea93ccp161cc4jsndf3bef7811dc")
                                .build();

                        Response response = client.newCall(request).execute();

                        String JsondData=response.body().string();
                        JSONObject jsonObject=new JSONObject(JsondData);
                        showResponse(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }
            }).start();
        }
        private void showResponse(JSONObject response) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject result= null;
                    JSONObject cheapest_price1=null;
                    JSONArray cheapest_price=null;
                    double temp_gas=Integer.MAX_VALUE,temp_mid=Integer.MAX_VALUE,temp_pre=Integer.MAX_VALUE,temp_dis=Integer.MAX_VALUE;

                    try {
                        result =(JSONObject) response.get("result");
                        result =(JSONObject) result.get("state");
                        cheapest_price1= (JSONObject) response.get("result");
                        cheapest_price=   cheapest_price1.getJSONArray("cities");
                        for(int i=0; i<cheapest_price1.length();i++)
                        {
                            temp_gas=Math.min(temp_gas,Double.parseDouble(((JSONObject)cheapest_price.get(i)).getString("gasoline")));
                            temp_mid=Math.min(temp_mid,Double.parseDouble(((JSONObject)cheapest_price.get(i)).getString("midGrade")));
                            temp_pre=Math.min(temp_pre,Double.parseDouble(((JSONObject)cheapest_price.get(i)).getString("premium")));
                            temp_dis=Math.min(temp_dis,Double.parseDouble(((JSONObject)cheapest_price.get(i)).getString("diesel")));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        String gasoline=result.getString("gasoline");
                        String midGrade=result.getString("midGrade");
                        String premium=result.getString("premium");
                        String diesel=result.getString("diesel");
                        responseText.setText("Average_gasoline_price: $"+gasoline+"\n"+"Average_midGrade_price: $"+midGrade+"\n"+"Average_premium_price: $"+premium+"\n"+
                                "Average_diesel_price: $"+diesel+"\n"+"Lowest_gasoline_price: $"+temp_gas+"\n"+"Lowest_midGrade_price: $"+temp_mid+"\n"+"Lowest_premium_price: $"+temp_pre+"\n"+"Lowest_diesel_price: $"+temp_dis
                        );
//                        responseText.setText(midGrade);
//                        responseText.setText(premium);
//                        responseText.setText(diesel);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
//    {
//        "status": 0,
//            "msg": "ok",
//            "result": {
//        "province": "河南",
//                "oil90": "7.00",
//                "oil93": "7.42",
//                "oil97": "7.84",
//                "oil0": "7.36",
//                "updatetime": "2028-01-20 06:00:02"
//    }
//    }
        private void parseJsoDataAndShow(String jsonStr) throws JSONException {
            JSONObject jsonObject=new JSONObject(jsonStr);
            String city=jsonObject.optString("province");
            String Oil89=jsonObject.optString("oil89");
            String oil92=jsonObject.optString("oil92");
            String Oil90=jsonObject.optString("oil90");
            String Oil95=jsonObject.optString("oil95");
            String Oil98=jsonObject.optString("oil98");
            String Oil0=jsonObject.optString("oil0");
            String Oil93=jsonObject.optString("oil93");
            String Oil97=jsonObject.optString("oil97");
            String updatetime=jsonObject.optString("updatetime");

            //显示Json数据
        }


    }
