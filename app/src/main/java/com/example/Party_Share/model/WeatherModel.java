package com.example.Party_Share.model;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherModel {
    final public static WeatherModel instance = new WeatherModel();

    final String BASE_URL = "https://api.open-meteo.com";
    Retrofit retrofit;
    WeatherApi weatherApi;

    private WeatherModel() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        weatherApi = retrofit.create(WeatherApi.class);
    }

    public LiveData<WeatherDetails> getCurrentWeatherForLocation(double latitude, double longitude) {
        MutableLiveData<WeatherDetails> data = new MutableLiveData<>();

        Call<WeatherSearchResult> call = weatherApi.getCurrentWeatherForLocation(latitude, longitude);
        call.enqueue(new Callback<WeatherSearchResult>() {
            @Override
            public void onResponse(Call<WeatherSearchResult> call, Response<WeatherSearchResult> response) {
                if (response.isSuccessful()) {
                    WeatherSearchResult res = response.body();
                    data.setValue(res.getCurrentWeather());
                } else {
                    Log.d("TAG", "----- getCurrentWeatherForLocation response error");
                }
            }

            @Override
            public void onFailure(Call<WeatherSearchResult> call, Throwable t) {
                Log.d("TAG", "----- getCurrentWeatherForLocation fail");
            }
        });
        return data;
    }

}
