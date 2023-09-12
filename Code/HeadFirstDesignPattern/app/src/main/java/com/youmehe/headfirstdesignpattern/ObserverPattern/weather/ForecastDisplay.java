package com.youmehe.headfirstdesignpattern.ObserverPattern.weather;

public class ForecastDisplay implements WeatherObserver, DisplayElement {
    float mCurrentPressure = 29.92f;
    float mLastPressure;
    WeatherData mWeatherData;

    public ForecastDisplay(WeatherData weatherData) {
        mWeatherData = weatherData;
        mWeatherData.registerObserver(this);
    }

    @Override
    public void display() {
        System.out.println("Forecast:");
        if (mCurrentPressure > mLastPressure) {
            System.out.println("Improving weather on the way!");
        } else if (Math.abs(mCurrentPressure - mLastPressure) <= 1e-6) {
            System.out.println("More fo same!");
        } else {
            System.out.println("Watch out for cooler, rainy weather!");
        }
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        mLastPressure = mCurrentPressure;
        mCurrentPressure = pressure;
        display();
    }
}
