package com.zm.piboxservice.sensor;

import com.zm.piboxservice.rpi.GPIO;

import java.util.Random;

public class Temperature implements Sensor {

    private GPIO _pin;
    private Random _random;
    private double _temp;

    private boolean _heating;
    private boolean _cooling;

    public Temperature(GPIO pin) {
        _pin = pin;
        _random = new Random();
        _temp = 75;
    }

    @Override
    public double read() {
        return _temp;
    }

    @Override
    public void call() {
        int r = _random.nextInt(10000000);
        if(r == 0) {
           double adj = _random.nextDouble();
           boolean coolingAllowed = !(!_cooling && _heating);
           boolean heatingAllowed = !(_cooling && !_heating);
           if((coolingAllowed && _random.nextBoolean()) || !heatingAllowed) {
               adj *= -1;
           }
            _temp += adj;
        }
    }

    public void usingHeater(boolean state) {
        _heating = state;
    }

    public void usingAC(boolean state) {
        _cooling = state;
    }
}
