package com.youmehe.headfirstdesignpattern.ObserverPattern.java;

import java.util.Observable;
import java.util.Observer;

public class StatisticsDisplay implements Observer, DisplayElement {
    float mMaxTemp = 0.0f;
    float mMinTemp = 200;
    float mTempSum = 0.0f;
    int mNumReadings;
    private Observable mObservable;

    public StatisticsDisplay(Observable observable) {
        mObservable = observable;
        mObservable.addObserver(this);
    }

    @Override
    public void display() {
        System.out.println("Avg/Max/Min temperature:\n" + (mTempSum / mNumReadings) + "/" + mMaxTemp + "/" + mMinTemp);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof WeatherData) {
            WeatherData weatherData = (WeatherData) observable;
            float temperature = weatherData.getTemperature();
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
}
