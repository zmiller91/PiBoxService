package com.zm.piboxservice.rpi;

public enum Pinout {

    LIGHT(1),
    TEMPERATURE(2),
    MOISTURE(3),
    PHOTORESISTOR(4)
    ;

    private int value;

    Pinout(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
