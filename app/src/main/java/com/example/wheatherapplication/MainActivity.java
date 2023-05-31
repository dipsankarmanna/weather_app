package com.example.wheatherapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.wheatherapplication.databinding.ActivityMainBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Wheather_Adapter wheather_adapter;
    ArrayList<wheather_model> list;
    LocationManager locationManager;
    int PERMISSION_CODE=1;
    String CityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(binding.getRoot());
        list=new ArrayList<>();
        wheather_adapter=new Wheather_Adapter(MainActivity.this,list);
        binding.weatherRecyler.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.weatherRecyler.setAdapter(wheather_adapter);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_DENIED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }
        SharedPreferences sh = getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String s1 = sh.getString("city", "kolkata");
        if(s1.length()!=0){
            binding.cityName.setText(s1);
            getWheatherInfo(s1);
        }
        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=binding.city.getText().toString();
                if (name.equals("")){
                    binding.city.setError("Please Enter Any City");
                }else {
                    binding.cityName.setText(name);
                    getWheatherInfo(name);
                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("city", name);
                    myEdit.commit();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
            if(grantResults.length>=0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String cityName(double longitude, double latitude){
        String cityname="Not found";
        Geocoder geocoder=new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses=geocoder.getFromLocation(latitude,longitude,10);
            for (Address adr:addresses){
                if(adr!=null){
                    String city = adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityname=city;
                    }else {
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this, "City not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.getStackTrace();
        }
        return cityname;
    }
    private void getWheatherInfo(String cityname){
        String url="http://api.weatherapi.com/v1/forecast.json?key=552f2a33006e4e82bc0192120220607&q="+cityname+"&days=1&aqi=no&alerts=no";
        binding.cityName.setText(cityname);
        RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //progress disable
                list.clear();
                try {
                    String temperature=response.getJSONObject("current").getString("temp_c");
                    binding.temp.setText(temperature+"°C");
                    int isDay=response.getJSONObject("current").getInt("is_day");
                    String condition=response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String icon=response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:"+icon).into(binding.tempIcon);
                    binding.condition.setText(condition);
                    if (isDay==1){
                        // binding.bac.setBackgroundResource(R.drawable.day);
                    }

                    JSONObject forecastobj=response.getJSONObject("forecast");
                    JSONObject forecast=forecastobj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray=forecast.getJSONArray("hour");
                    for (int i=0;i<hourArray.length();i++){
                        JSONObject hourobj=hourArray.getJSONObject(i);
                        String time=hourobj.getString("time");
                        String tempar=hourobj.getString("temp_c");
                        String img=hourobj.getJSONObject("condition").getString("icon");
                        String wind=hourobj.getString("wind_kph");
                        list.add(new wheather_model(time,tempar,img,wind));
                    }
                    wheather_adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Please Enter a valid city Name", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}