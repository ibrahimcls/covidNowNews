package com.clsmob.covidnownews;

import com.clsmob.covidnownews.models.ContinentData;
import com.clsmob.covidnownews.models.CoronaNews;
import com.clsmob.covidnownews.models.CountriesData;
import com.clsmob.covidnownews.models.ResponseModel;
import com.clsmob.covidnownews.models.TotalData;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface ApiService {

    String TOKEN = "apikey 66Ad6zbI6UDKSlKHH3DhNa:5vq9eZBAGH7PSX3d9f9rxI";
    String contentType = "application/json";

    @Headers({
            "content-type:"+contentType,
            "authorization:"+TOKEN
    })
    @GET("/corona/countriesData")
    Call<ResponseModel<ArrayList<CountriesData>>> getCountriesData();

    @Headers({
            "content-type:"+contentType,
            "authorization:"+TOKEN
    })
    @GET("/corona/totalData")
    Call<ResponseModel<TotalData>> getTotalData();

    @Headers({
            "content-type:"+contentType,
            "authorization:"+TOKEN
    })
    @GET("/corona/continentData")
    Call<ResponseModel<ArrayList<ContinentData>>> getContinentData();

    @Headers({
            "content-type:"+contentType,
            "authorization:"+TOKEN
    })
    @GET("/corona/coronaNews")
    Call<ResponseModel<ArrayList<CoronaNews>>> getCoronaNews();

}
