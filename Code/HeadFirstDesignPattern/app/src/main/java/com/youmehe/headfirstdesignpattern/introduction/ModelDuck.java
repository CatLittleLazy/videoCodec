package com.youmehe.headfirstdesignpattern.introduction;

public class ModelDuck extends Duck{
    public ModelDuck() {
        quackBehavior = new Quack();
        flyBehavior = new FlyNoWay();
    }

    @Override
    public void display() {
        System.out.println("I'm a model duck!\n我可是模型鸭!");
    }
}
