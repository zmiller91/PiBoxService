package com.zm.piboxservice.sensor;

import com.zm.piboxservice.rpi.GPIO;

import java.util.Random;
import java.util.TimerTask;

public class Moisture extends TimerTask implements Sensor {

    private GPIO _pin;
    private double _density;
    private Random _random;
    private final SensorListener[] listeners;

    public Moisture(GPIO pin) {
        this(pin, new SensorListener[0]);
    }

    public Moisture(GPIO pin, SensorListener... listeners) {
        _pin = pin;
        _random = new Random();
        _density = resetDensity();
        this.listeners = listeners;
    }

    @Override
    public double read() {
        return _density;
    }

    @Override
    public void run() {
        int r = _random.nextInt(10);
        if(r == 0) {
            _density = Math.max(0, _density - (_random.nextDouble()));
            for(SensorListener sl : listeners) {
                sl.onSensorUpdate(this);
            }

        }
    }

    // Used for testing -- hint to the sensor that an irrigation cycle just ended
    public double resetDensity() {
        _density = _random.nextInt(25) + 75;
        return _density;
    }
}
