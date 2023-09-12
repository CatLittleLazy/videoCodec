package com.youmehe.headfirstdesignpattern.ObserverPattern.weather;

public class CurrentConditionDisplay implements WeatherObserver, DisplayElement {
    float mTemperature;
    float mHumidity;
    WeatherSubject mWeatherSubject;

    public CurrentConditionDisplay(WeatherSubject weatherSubject) {
        // 这里也可以不保留对subject的持有，不过持有的化方便后面进行对subject的取消
        mWeatherSubject = weatherSubject;
        mWeatherSubject.registerObserver(this);
    }

    @Override
    public void display() {
        System.out.println("Current conditions Panel\n" + mTemperature + "F degrees and " + mHumidity + "% humidity");
    }

    @Override
    public void update(float temperature, float humidity, float pressure) {
        mTemperature = temperature;
        mHumidity = humidity;
        display();
    }
}
