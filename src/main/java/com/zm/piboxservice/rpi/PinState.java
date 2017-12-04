package com.zm.piboxservice.rpi;

public enum PinState {

    LOW(0),
    HIGH(1)

    ;

    private int value;

    PinState(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}
