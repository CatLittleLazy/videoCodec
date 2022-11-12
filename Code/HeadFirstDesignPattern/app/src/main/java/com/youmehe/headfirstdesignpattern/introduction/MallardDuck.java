package com.youmehe.headfirstdesignpattern.introduction;

public class MallardDuck extends Duck{
    public MallardDuck() {
        quackBehavior = new Quack();
        flyBehavior = new FlyWithWings();
    }

    @Override
    public void display() {
        System.out.println("I'm a real Mallard duck!\n我可是地道的绿头鸭!");
    }
}
