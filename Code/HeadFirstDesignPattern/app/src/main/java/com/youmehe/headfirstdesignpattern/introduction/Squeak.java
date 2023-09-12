package com.youmehe.headfirstdesignpattern.introduction;

public class Squeak implements QuackBehavior{
    @Override
    public void quack() {
        System.out.println("Squeak, Squeak!!");
    }
}
