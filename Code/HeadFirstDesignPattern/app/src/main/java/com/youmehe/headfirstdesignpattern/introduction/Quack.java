package com.youmehe.headfirstdesignpattern.introduction;

public class Quack implements QuackBehavior{
    @Override
    public void quack() {
        System.out.println("Quack, Quack!!");
    }
}
