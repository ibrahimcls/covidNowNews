package com.clsmob.covidnownews.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clsmob.covidnownews.R;
import com.clsmob.covidnownews.models.ContinentData;
import com.clsmob.covidnownews.models.ResponseModel;

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

public class ContinentDataActivity extends BaseActivity {
    Realm realm;
    String now;
    public static ArrayList<ContinentData> continentsDataList = new ArrayList<>();
    RecyclerView recyclerView;
    ProgressBar progressBar_continentData;
    ContinentDataListAdapter continentDataListAdapter = new ContinentDataListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continent_data);
        realm = Realm.getDefaultInstance();

        Date date = new Date();
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        now= DateFor.format(date);
        recyclerView = findViewById(R.id.continentData_rv);

        progressBar_continentData = findViewById(R.id.progressBar_continentData);
        recyclerView.setLayoutManager(new LinearLayoutManager(_activity));
        recyclerView.setAdapter(continentDataListAdapter);

        if(!now.equals(sharedPreferences.getString("dateContinentData","")))
            getContinentData();
        else
            getContinentDataOffline();

    }

    private void getContinentDataOffline() {
        RealmResults<ContinentData> results  = realm.where(ContinentData.class).findAll();
        continentsDataList.clear();
        continentsDataList.addAll(results);
    }

    public void startLoad() {
        recyclerView.setEnabled(false);
        progressBar_continentData.setVisibility(View.VISIBLE);
    }

    public void endLoad() {
        recyclerView.setEnabled(true);
        progressBar_continentData.setVisibility(View.INVISIBLE);
    }

    private void getContinentData() {
        startLoad();
        apiService.getContinentData().enqueue(new Callback<ResponseModel<ArrayList<ContinentData>>>() {
            @Override
            public void onResponse(Call<ResponseModel<ArrayList<ContinentData>>> call, Response<ResponseModel<ArrayList<ContinentData>>> response) {

                if (response.body().success && response.body().result != null) {
                    continentsDataList.addAll(response.body().result);
                    continentDataListAdapter.notifyDataSetChanged();
                    endLoad();

                    sharedPreferences.edit().putString("dateContinentData",now).apply();

                    realm.beginTransaction();
                    realm.copyToRealm(continentsDataList);
                    realm.commitTransaction();
                } else {
                    Toast.makeText(_activity, "Veri alınamadı", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel<ArrayList<ContinentData>>> call, Throwable t) {
                Toast.makeText(_activity, "Veri alınamadı", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ContinentDataListAdapter extends RecyclerView.Adapter<ContinentDataListAdapter.ContinentDataListViewHolder> {
        @NonNull
        @Override
        public ContinentDataListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_data_item, parent, false);
            return new ContinentDataListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContinentDataListViewHolder holder, int position) {
            ContinentData continentData = continentsDataList.get(position);
            holder.continentName_tv.setText(continentData.getContinent());
            holder.continent_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(_activity, DataDetailsActivity.class);

                    intent.putExtra("dataTitle",getString( R.string.continent));
                    intent.putExtra("data", continentData.getContinent());
                    intent.putExtra("totalCases", continentData.getTotalCases());
                    intent.putExtra("totalDeaths", continentData.getTotalDeaths());
                    intent.putExtra("newDeaths", continentData.getNewDeaths());
                    intent.putExtra("totalRecovered", continentData.getTotalRecovered());
                    intent.putExtra("activeCases", continentData.getActiveCases());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return continentsDataList.size();
        }

        public class ContinentDataListViewHolder extends RecyclerView.ViewHolder {
            TextView continentName_tv;
            FrameLayout continent_layout;

            public ContinentDataListViewHolder(@NonNull View itemView) {
                super(itemView);
                continentName_tv = itemView.findViewById(R.id.dataName_tv);
                continent_layout = itemView.findViewById(R.id.data_layout);
            }
        }
    }
}