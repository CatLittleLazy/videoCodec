package com.youmehe.headfirstdesignpattern.ObserverPattern.weather;
import java.util.ArrayList;
import java.util.List;

public class WeatherData implements WeatherSubject {

    float mTemperature;
    float mHumidity;
    float mPressure;
    List<WeatherObserver> mObservers;

    public WeatherData() {
        mObservers = new ArrayList<>();
    }

    @Override
    public void registerObserver(WeatherObserver observer) {
        mObservers.add(observer);
    }

    @Override
    public void removeObserver(WeatherObserver observer) {
        int i = mObservers.indexOf(observer);
        if (i >= 0) mObservers.remove(observer);
    }

    @Override
    public void notifyObserver() {
        for (WeatherObserver weatherObserver : mObservers) {
            weatherObserver.update(mTemperature, mHumidity, mPressure);
        }
    }

    public void measurementsChanged() {
        notifyObserver();
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
