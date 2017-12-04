package com.zm.piboxservice.rpi;

public class GPIO {

    private Pinout _pin;
    private PinState _state;

    public GPIO(Pinout pin) {
        _pin = pin;
        _state = PinState.LOW;
    }

    public void setState(PinState state) {
        _state = state;
    }

    public PinState getState() {
        return _state;
    }
}
