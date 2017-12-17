package com.zm.piboxservice.sensor;

import com.zm.piboxservice.activity.SensorListener;
import com.zm.piboxservice.rpi.GPIO;

import java.util.Random;
import java.util.TimerTask;

public class Temperature extends TimerTask implements Sensor {

    private GPIO _pin;
    private final SensorListener[] listeners;
    private Random _random;
    private double _temp;

    private boolean _heating;
    private boolean _cooling;

    public Temperature(GPIO pin) {
        this(pin, new SensorListener[0]);
    }

    public Temperature(GPIO pin, SensorListener ... listeners) {
        _pin = pin;
        _random = new Random();
        _temp = 75;
        this.listeners = listeners;
    }

    @Override
    public double read() {
        return _temp;
    }

    @Override
    public void run() {
        int r = _random.nextInt(10);
        if(r == 0) {
           double adj = _random.nextDouble();
           boolean coolingAllowed = !(!_cooling && _heating);
           boolean heatingAllowed = !(_cooling && !_heating);
           if((coolingAllowed && _random.nextBoolean()) || !heatingAllowed) {
               adj *= -1;
           }
            _temp += adj;
           for(SensorListener sl : listeners) {
               sl.onSensorUpdate(this);
           }
        }
    }

    public void usingHeater(boolean state) {
        _heating = state;
    }

    public void usingAC(boolean state) {
        _cooling = state;
    }
}
