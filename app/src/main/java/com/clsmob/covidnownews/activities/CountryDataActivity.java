package com.clsmob.covidnownews.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clsmob.covidnownews.R;
import com.clsmob.covidnownews.models.CountriesData;
import com.clsmob.covidnownews.models.ResponseModel;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CountryDataActivity extends BaseActivity {
    Realm realm;
    String now;
    RecyclerView recyclerView;
    ProgressBar progressBar_countryData;
    TextInputEditText search_et;
    public static ArrayList<CountriesData> countriesDataList = new ArrayList<>();
    ArrayList<CountriesData> showCountriesDataList = new ArrayList<>();
    CountryDataListAdapter countryDataListAdapter = new CountryDataListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_data);
        realm = Realm.getDefaultInstance();

        Date date = new Date();
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        now= DateFor.format(date);
        recyclerView = findViewById(R.id.countryData_rv);
        search_et = findViewById(R.id.search_et);
        progressBar_countryData = findViewById(R.id.progressBar_countryData);
        recyclerView.setLayoutManager(new LinearLayoutManager(_activity));
        recyclerView.setAdapter(countryDataListAdapter);

        search_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println("smth");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("smth");
            }

            @Override
            public void afterTextChanged(Editable s) {
                ArrayList<CountriesData> searchList = new ArrayList<>();

                if (s.toString().toLowerCase().equals("")) {
                    showCountriesDataList = countriesDataList;
                    countryDataListAdapter.notifyDataSetChanged();
                    return;
                }

                for (CountriesData country : countriesDataList) {
                    if (country.getCountry().toLowerCase().contains(s.toString().toLowerCase())) {
                        searchList.add(country);
                    }
                }
                showCountriesDataList.clear();
                showCountriesDataList.addAll(searchList);
                countryDataListAdapter.notifyDataSetChanged();

            }
        });

        if(!now.equals(sharedPreferences.getString("dateCountryData","")))
            getCountryData();
        else
            getCountryDataOffline();

    }

    private void getCountryDataOffline() {
        RealmResults<CountriesData> results  = realm.where(CountriesData.class).findAll();
        countriesDataList.clear();
        countriesDataList.addAll(results);
        showCountriesDataList.clear();
        showCountriesDataList.addAll(results);
    }

    public void startLoad(){
        recyclerView.setEnabled(false);
        search_et.setEnabled(false);
        progressBar_countryData.setVisibility(View.VISIBLE);
    }
    public void endLoad(){
        recyclerView.setEnabled(true);
        search_et.setEnabled(true);
        progressBar_countryData.setVisibility(View.INVISIBLE);
    }

    private void getCountryData() {
        startLoad();
        apiService.getCountriesData().enqueue(new Callback<ResponseModel<ArrayList<CountriesData>>>() {
            @Override
            public void onResponse(Call<ResponseModel<ArrayList<CountriesData>>> call, Response<ResponseModel<ArrayList<CountriesData>>> response) {

                if (response.body().success && response.body().result != null) {
                    countriesDataList.addAll(response.body().result);
                    showCountriesDataList.addAll(response.body().result);
                    countryDataListAdapter.notifyDataSetChanged();
                    endLoad();
                    sharedPreferences.edit().putString("dateCountryData",now).apply();

                    realm.beginTransaction();
                    realm.copyToRealm(countriesDataList);
                    realm.commitTransaction();
                } else {
                    Toast.makeText(_activity, "Veri alınamadı", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<ArrayList<CountriesData>>> call, Throwable t) {
                Toast.makeText(_activity, "Veri alınamadı", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class CountryDataListAdapter extends RecyclerView.Adapter<CountryDataListAdapter.CountryDataListViewHolder> {
        @NonNull
        @Override
        public CountryDataListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_data_item, parent, false);
            return new CountryDataListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CountryDataListViewHolder holder, int position) {
            CountriesData countryData =  showCountriesDataList.get(position);
            holder.countryName_tv.setText(countryData.getCountry());
            holder.country_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(_activity, DataDetailsActivity.class);
                    intent.putExtra("dataTitle",getString(R.string.country));
                    intent.putExtra("data",countryData.getCountry());
                    intent.putExtra("totalCases",countryData.getTotalCases());
                    intent.putExtra("totalDeaths",countryData.getTotalDeaths());
                    intent.putExtra("newDeaths",countryData.getNewDeaths());
                    intent.putExtra("totalRecovered",countryData.getTotalRecovered());
                    intent.putExtra("activeCases",countryData.getActiveCases());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return showCountriesDataList.size();
        }

        public class CountryDataListViewHolder extends RecyclerView.ViewHolder {
            TextView countryName_tv;
            FrameLayout country_layout;

            public CountryDataListViewHolder(@NonNull View itemView) {
                super(itemView);
                countryName_tv = itemView.findViewById(R.id.dataName_tv);
                country_layout = itemView.findViewById(R.id.data_layout);
            }
        }
    }
}