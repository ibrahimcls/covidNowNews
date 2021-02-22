package com.clsmob.covidnownews.activities;

import android.os.Bundle;
import android.widget.TextView;

import com.clsmob.covidnownews.R;

import androidx.appcompat.app.AppCompatActivity;

public class DataDetailsActivity extends AppCompatActivity {
    String data,dataTitle, totalCases, totalDeaths, newDeaths, totalRecovered, activeCases;
    TextView data_tv, totalCases_tv, totalDeaths_tv, newDeaths_tv, totalRecovered_tv, activeCases_tv, dataTitle_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_details);

        dataTitle = getIntent().getStringExtra("dataTitle");
        data = getIntent().getStringExtra("data");
        totalCases = getIntent().getStringExtra("totalCases");
        totalDeaths = getIntent().getStringExtra("totalDeaths");
        newDeaths = getIntent().getStringExtra("newDeaths");
        totalRecovered = getIntent().getStringExtra("totalRecovered");
        activeCases = getIntent().getStringExtra("activeCases");

        dataTitle_tv = findViewById(R.id.dataTitle_tv);
        data_tv = findViewById(R.id.data_tv);
        totalCases_tv = findViewById(R.id.totalCases_tv);
        totalDeaths_tv = findViewById(R.id.totalDeaths_tv);
        newDeaths_tv = findViewById(R.id.newDeaths_tv);
        totalRecovered_tv = findViewById(R.id.newCases_tv);
        activeCases_tv = findViewById(R.id.activeCases_tv);


        setText(dataTitle_tv,dataTitle);
        setText(data_tv,data);
        setText(totalCases_tv,totalCases);
        setText(totalDeaths_tv,totalDeaths);
        setText(newDeaths_tv,newDeaths);
        setText(totalRecovered_tv,totalRecovered);
        setText(activeCases_tv,activeCases);

    }
    public void setText(TextView textView,String text){
        if(text.equals(""))
            textView.setText("-");
        else textView.setText(text);
    }
}

