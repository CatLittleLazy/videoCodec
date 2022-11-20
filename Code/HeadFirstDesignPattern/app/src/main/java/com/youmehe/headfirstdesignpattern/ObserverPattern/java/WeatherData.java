package com.youmehe.headfirstdesignpattern.ObserverPattern.java;

import java.util.Observable;

public class WeatherData extends Observable {
    private float mTemperature;
    private float mHumidity;
    private float mPressure;

    public WeatherData() {}

    public void measurementsChanged() {
        setChanged();
        notifyObservers();
    }

    public void setMeasurements(float temperature, float humidity, float pressure) {
        mTemperature = temperature;
        mHumidity = humidity;
        mPressure = pressure;
        measurementsChanged();
    }

    public float getTemperature() {
        return mTemperature;
    }

    public float getHumidity() {
        return mHumidity;
    }

    public float getPressure() {
        return mPressure;
    }
}
