package com.example.Party_Share.model;

public class WeatherDetails {
    double temperature;
    double windspeed;

    public WeatherDetails(double temperature, double windspeed) {
        this.temperature = temperature;
        this.windspeed = windspeed;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getWindSpeed() {
        return windspeed;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setWindSpeed(double windspeed) {
        this.windspeed = windspeed;
    }
}
