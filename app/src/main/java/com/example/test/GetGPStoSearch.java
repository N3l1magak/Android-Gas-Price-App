package com.example.test;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetGPStoSearch extends AppCompatActivity implements View.OnClickListener {

    public static final int LOCATION_CODE = 301;
    private LocationManager locationManager;
    private String locationProvider = null;
    private EditText currentlocation;
    private Button Butt_Request_GPS;
    private TextView response_text_GPS;
    private HashMap<String,String> Statemap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_gpsto_search);

        Statemap.put("District of Columbia","DC");
        Statemap.put("California","CA");
        Statemap.put("New York","NY");

        currentlocation=(EditText) findViewById(R.id.get_state);
        Butt_Request_GPS=(Button)findViewById(R.id.send_request_forGPS);
        response_text_GPS=(TextView) findViewById(R.id.response_text_GPS);
        getLocation();
        Butt_Request_GPS.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) { //重写点击事件的函数
        if (v.getId() == R.id.send_request_forGPS) {
            sendRequestWithOkHttp();
        }
    }

    private void getLocation(){
        //1.获取位置管理器 位置信息管理对象
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //2.获取位置提供器的列表，GPS或是NetWorkm GPS 定位速度快，耗电少，精度不高；Network速度慢，高电耗，精度高
        List<String> providers = locationManager.getProviders(true);

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            Log.v("TAG", "定位方式GPS");
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
            Log.v("TAG", "定位方式Network");
        }else { // PROVIDER IS NULL, 定位失败
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //获取权限（如果没有开启权限，会弹出对话框，询问是否开启权限）
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
            } else {
                //3.获取上次的位置，一般第一次运行，此值为null
                Location location = locationManager.getLastKnownLocation(locationProvider);
                if (location!=null){
                    Toast.makeText(this, location.getLongitude() + " " +
                            location.getLatitude() + "",Toast.LENGTH_SHORT).show();
                    Log.v("TAG", "获取上次的位置-经纬度："+location.getLongitude()+"   "+location.getLatitude());
                    getAddress(location);

                }else{ //
                    //监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                    locationManager.requestLocationUpdates(locationProvider, 3000, 1,locationListener);
                }
            }
        } else {
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location!=null){
                Toast.makeText(this, location.getLongitude() + " " +
                        location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                Log.v("TAG", "获取上次的位置-经纬度："+location.getLongitude()+"   "+location.getLatitude());
                getAddress(location);

            }else{
                //监视地理位置变化，第二个和第三个参数分别为更新的最短时间minTime和最短距离minDistace
                locationManager.requestLocationUpdates(locationProvider, 3000, 1,locationListener);
            }
        }
    }

    public LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
        }
        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
        }
        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                //如果位置发生变化，重新显示地理位置经纬度
                Toast.makeText(GetGPStoSearch.this, location.getLongitude() + " " +
                        location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                Log.v("TAG", "监视地理位置变化-经纬度："+location.getLongitude()+"   "+location.getLatitude());
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == getPackageManager().PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "申请权限", Toast.LENGTH_LONG).show();
                    try {
                        List<String> providers = locationManager.getProviders(true);
                        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                            // If using Network
                            locationProvider = LocationManager.NETWORK_PROVIDER;

                        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
                            // If using GPS
                            locationProvider = LocationManager.GPS_PROVIDER;
                        }
                        Location location = locationManager.getLastKnownLocation(locationProvider);
                        if (location != null) {
                            Toast.makeText(this, location.getLongitude() + " " +
                                    location.getLatitude() + "", Toast.LENGTH_SHORT).show();
                            Log.v("TAG", "Get latest longitude and latitude：" + location.getLongitude() + "   " + location.getLatitude());
                        } else {
                            // Monitor the change of geographic location, the second and third parameters are the updated minimum time minTime and the shortest distance minDistace respectively
                            locationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
                        }

                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "缺少权限", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }


    // Get address information: city, street, etc.
    private List<Address> getAddress(Location location) {
        List<Address> result = null;
        try {
            if (location != null) {
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                result = gc.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
                Toast.makeText(this, "Get address information："+result.toString(), Toast.LENGTH_LONG).show();
                Log.v("TAG", "Get address information："+result.toString());
                currentlocation.setText(Statemap.get(result.get(0).getAdminArea().toString()));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
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
                            .url("https://gas-price.p.rapidapi.com/stateUsaPrice?state=" + currentlocation.getText().toString())
                            .get()
                            .addHeader("x-rapidapi-host", "gas-price.p.rapidapi.com")
                            .addHeader("x-rapidapi-key", "hidden")
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
                    response_text_GPS.setText("Average_gasoline_price: $"+gasoline+"\n"+"Average_midGrade_price: $"+midGrade+"\n"+"Average_premium_price: $"+premium+"\n"+
                            "Average_diesel_price: $"+diesel+"\n"+"Lowest_gasoline_price: $"+temp_gas+"\n"+"Lowest_midGrade_price: $"+temp_mid+"\n"+"Lowest_premium_price: $"+temp_pre+"\n"+"Lowest_diesel_price: $"+temp_dis);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
