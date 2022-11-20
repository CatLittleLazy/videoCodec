package com.youmehe.headfirstdesignpattern.ObserverPattern.java;

import java.util.Observable;
import java.util.Observer;

public class ForecastDisplay implements Observer,DisplayElement {
    private float mCurrentPressure = 29.92f;
    private float mLastPressure;
    private Observable mObservable;

    public ForecastDisplay(Observable observable) {
        mObservable = observable;
        mObservable.addObserver(this);
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
    public void update(Observable observable, Object o) {
        if (observable instanceof WeatherData) {
            WeatherData weatherData = (WeatherData) observable;
            mLastPressure = mCurrentPressure;
            mCurrentPressure = weatherData.getPressure();
            display();
        }
    }
}
