package com.youmehe.headfirstdesignpattern.ObserverPattern.model;

public interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObserver();
}
