package com.zm.piboxservice.sensor;

import com.zm.piboxservice.rpi.GPIO;

import java.util.Random;

public class Moisture implements Sensor {

    private GPIO _pin;
    private double _density;
    private Random _random;

    public Moisture(GPIO pin) {
        _pin = pin;
        _random = new Random();
        _density = resetDensity();
    }

    @Override
    public double read() {
        return _density;
    }

    @Override
    public void call() {
        int r = _random.nextInt(1000000);
        if(r == 0) {
            _density = Math.max(0, _density - (_random.nextDouble()));
        }
    }

    // Used for testing -- hint to the sensor that an irrigation cycle just ended
    public double resetDensity() {
        _density = _random.nextInt(25) + 75;
        return _density;
    }
}
