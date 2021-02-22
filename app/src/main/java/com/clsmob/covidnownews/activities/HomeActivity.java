package com.clsmob.covidnownews.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.clsmob.covidnownews.R;
import com.clsmob.covidnownews.models.ResponseModel;
import com.clsmob.covidnownews.models.TotalData;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {
    Realm realm;
    String now;
    TotalData totalData = new TotalData();
    TextView totalDeaths_tv, totalCases_tv, totalRecovered_tv;
    TextView coronaNews_tv, countryData_tv, continentData_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        realm = Realm.getDefaultInstance();

        Date date = new Date();
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        now= DateFor.format(date);

        totalDeaths_tv = findViewById(R.id.alltotalDeaths_tv);
        totalCases_tv = findViewById(R.id.alltotalCases_tv);
        totalRecovered_tv = findViewById(R.id.alltotalRecovered_tv);
        coronaNews_tv = findViewById(R.id.coronaNews_tv);
        countryData_tv = findViewById(R.id.countryData_tv);
        continentData_tv = findViewById(R.id.continentData_tv);
        coronaNews_tv.setOnClickListener(v -> {
            Intent intent = new Intent(_activity, CoronaNewsActivity.class);
            startActivity(intent);
        });
        countryData_tv.setOnClickListener(v -> {
            Intent intent = new Intent(_activity,CountryDataActivity.class);
            startActivity(intent);
        });
        continentData_tv.setOnClickListener(v -> {
            Intent intent = new Intent(_activity,ContinentDataActivity.class);
            startActivity(intent);
        });

        if(!now.equals(sharedPreferences.getString("dateTotalData","")))
            getTotal();
        else
            getTotalOffline();
    }

    private void getTotalOffline() {
        totalData = realm.where(TotalData.class).findFirst();

        totalDeaths_tv.setText(totalData.getTotalDeaths());
        totalCases_tv.setText(totalData.getTotalCases());
        totalRecovered_tv.setText(totalData.getTotalRecovered());
    }

    private void getTotal() {
        apiService.getTotalData().enqueue(new Callback<ResponseModel<TotalData>>() {
            @Override
            public void onResponse(Call<ResponseModel<TotalData>> call, Response<ResponseModel<TotalData>> response) {
                System.out.println("onResponse");
                if (response.body().success && response.body().result != null) {
                    totalData = response.body().result;
                    totalDeaths_tv.setText(totalData.getTotalDeaths());
                    totalCases_tv.setText(totalData.getTotalCases());
                    totalRecovered_tv.setText(totalData.getTotalRecovered());
                    sharedPreferences.edit().putString("dateTotalData",now).apply();

                    realm.beginTransaction();
                    realm.copyToRealm(totalData);
                    realm.commitTransaction();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<TotalData>> call, Throwable t) {

                System.out.println(t.getLocalizedMessage());
            }
        });
    }
}