package com.example.test.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.Buffer;

public class NetUtil {
    public static String Base_URL="https://api.jisuapi.com/oil/query";
    public static String app_key="3f17253cd3cffbfc";


    public static String doGet(String url)  {
        String result="";
        HttpURLConnection httpURLConnection=null;
        BufferedReader bufferedReader=null;
        String bookJsonString=null;


        try{
            //1. 建立连接

            URL requesturl= new URL(url);
            httpURLConnection=(HttpURLConnection) requesturl.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestProperty("charset","utf-8");
            httpURLConnection.connect();
            //2. 获取二进制流(二进制)
            InputStream inputStream=httpURLConnection.getInputStream();

            //3. 将二进制流包装 （转换成为人懂得）
            BufferedReader bufferedReader1= new BufferedReader (new InputStreamReader(inputStream));
            //4. 从BufferReader中读取一行一行String字符串， 用Stringbuilder接受
            String line;
            StringBuilder stringBuilder=new StringBuilder();
            while((line=bufferedReader.readLine())!=null){
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            if (stringBuilder.length()==0 ){
                return null;
            }
            result=stringBuilder.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static  String GetgasofCity(String province){

        // 拼接出get请求的完整url
        String gasUrl=Base_URL+"?"+"appkey="+app_key+"&"+"province="+province;
        Log.d("URL","GasURL"+gasUrl);
        String gasresult=doGet(gasUrl);
        return gasresult;
    }

}
