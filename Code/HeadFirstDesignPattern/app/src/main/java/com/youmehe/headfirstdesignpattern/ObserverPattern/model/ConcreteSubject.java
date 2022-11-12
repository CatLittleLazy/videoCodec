package com.youmehe.headfirstdesignpattern.ObserverPattern.model;

public class ConcreteSubject implements Subject{
    private int mState;

    int getState() {
        return mState;
    }

    void setState(int state) {
        mState = state;
    }

    @Override
    public void registerObserver(Observer observer) {

    }

    @Override
    public void removeObserver(Observer observer) {

    }

    @Override
    public void notifyObserver() {

    }
}
