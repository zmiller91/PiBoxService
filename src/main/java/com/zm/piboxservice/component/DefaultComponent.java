package com.zm.piboxservice.component;

import com.zm.piboxservice.rpi.GPIO;
import com.zm.piboxservice.rpi.PinState;

public class DefaultComponent implements Component {

    private String _id;
    private GPIO _gpio;
    private PinState _normalState;
    private boolean _override;

    public DefaultComponent(String id, GPIO gpio) {
        _id = id;
        _gpio = gpio;
        _override = false;
        _normalState = gpio.getState();
    }

    @Override
    public boolean turnOn() {
        _normalState = PinState.HIGH;
        if(!_override) {
            setState(_normalState);
            return true;
        }
        return false;
    }

    @Override
    public boolean turnOff() {
        _normalState = PinState.LOW;
        if(!_override) {
            setState(_normalState);
            return true;
        }
        return false;
    }

    @Override
    public void override(PinState state) {
        _override = true;
        setState(state);
    }

    @Override
    public void removeOverride() {
        _override = false;
        setState(_normalState);
    }

    @Override
    public PinState getState() {
        return _gpio.getState();
    }

    private void setState(PinState state) {
        PinState oldState = _gpio.getState();
        _gpio.setState(state);
        if(oldState != state) {
//            System.out.println(_id + " state change from " + oldState + " to " + state);
        }

    }
}
