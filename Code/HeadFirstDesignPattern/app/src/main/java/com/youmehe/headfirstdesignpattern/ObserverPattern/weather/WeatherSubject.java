package com.youmehe.headfirstdesignpattern.ObserverPattern.weather;

public interface WeatherSubject {
    void registerObserver(WeatherObserver observer);
    void removeObserver(WeatherObserver observer);
    void notifyObserver();
}
