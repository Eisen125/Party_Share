package com.example.Party_Share.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    @GET("/v1/forecast?current_weather=true")
    Call<WeatherSearchResult> getCurrentWeatherForLocation(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude
    );
    // https://api.open-meteo.com/v1/forecast?current_weather=true&latitude=XXX&longitude=YYY
}
