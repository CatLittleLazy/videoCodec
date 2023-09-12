package com.youmehe.headfirstdesignpattern.ObserverPattern.weather;

public class StatisticsDisplay implements WeatherObserver, DisplayElement {
    float mMaxTemp = 0.0f;
    float mMinTemp = 200;
    float mTempSum = 0.0f;
    int mNumReadings;
    WeatherData mWeatherData;

    public StatisticsDisplay(WeatherData weatherData) {
        mWeatherData = weatherData;
        mWeatherData.registerObserver(this);
    }

    @Override
    public void display() {
        System.out.println("Avg/Max/Min temperature:\n" + (mTempSum / mNumReadings) + "/" + mMaxTemp + "/" + mMinTemp);
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        mTempSum += temperature;
        mNumReadings++;
        if (temperature > mMaxTemp) {
            mMaxTemp = temperature;
        }
        if (temperature < mMinTemp) {
            mMinTemp = temperature;
        }
        display();
    }
}
