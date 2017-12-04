package com.zm.piboxservice.sensor;

import com.zm.piboxservice.rpi.GPIO;

import java.util.Random;

public class Photoresistor implements Sensor {

    private GPIO _pin;
    private Random _random;
    private double _value;
    private boolean _isLightOn;

    public Photoresistor(GPIO pin) {
        _pin = pin;
        _random = new Random();
        _value = 5;
        _isLightOn = false;
    }

    @Override
    public double read() {
        return _value;
    }

    @Override
    public void call() {

        int r = _random.nextInt(10000000);
        if(r == 0) {
            double adj = _random.nextDouble() * 0.01;
            if( _random.nextBoolean()) {
                adj *= -1;
            }
            _value += adj;
        }

        _value = _value > 100 ? 100 : _value < 0 ? 0 : _value;
    }

    public void setLight(boolean isOn) {
        if(_isLightOn != isOn) {
            _value = isOn ? 95 : 5;
        }

        _isLightOn = isOn;
    }
}
