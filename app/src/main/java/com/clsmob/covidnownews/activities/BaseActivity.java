package com.clsmob.covidnownews.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.clsmob.covidnownews.ApiService;
import com.google.gson.GsonBuilder;

import androidx.appcompat.app.AppCompatActivity;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseActivity extends AppCompatActivity {
    public static String BASE_URL = "https://api.collectapi.com/";
    public static String TOKEN = "apikey 66Ad6zbI6UDKSlKHH3DhNa:5vq9eZBAGH7PSX3d9f9rxI";
    public static String contentType = "application/json";

    public Retrofit retrofit;
    public Activity _activity;
    public ApiService apiService;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _activity =this;

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .build();
        apiService = retrofit.create(ApiService.class);


        Realm.init(this);

        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build());

        sharedPreferences = getSharedPreferences("covidNowNews", Context.MODE_PRIVATE);
    }
}