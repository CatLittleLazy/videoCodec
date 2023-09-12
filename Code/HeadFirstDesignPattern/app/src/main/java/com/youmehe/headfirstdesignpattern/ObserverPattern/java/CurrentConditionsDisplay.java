package com.youmehe.headfirstdesignpattern.ObserverPattern.java;

import java.util.Observable;
import java.util.Observer;

public class CurrentConditionsDisplay implements Observer, DisplayElement {
    Observable mObservable;
    private float mTemperature;
    private float mHumidity;

    public CurrentConditionsDisplay(Observable observable) {
        mObservable = observable;
        mObservable.addObserver(this);
    }

    @Override
    public void display() {
        System.out.println("Current conditions Panel\n" + mTemperature + "F degrees and " + mHumidity + "% humidity");
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof WeatherData) {
            WeatherData weatherData = (WeatherData) observable;
            mTemperature = weatherData.getTemperature();
            mHumidity = weatherData.getHumidity();
            display();
        }
    }
}
