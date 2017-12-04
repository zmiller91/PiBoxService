package com.zm.piboxservice.component;

import com.zm.piboxservice.rpi.PinState;

public interface Component {

    boolean turnOn(); // returns success
    boolean turnOff(); // returns success
    void override(PinState state);
    void removeOverride();
    PinState getState();
}
