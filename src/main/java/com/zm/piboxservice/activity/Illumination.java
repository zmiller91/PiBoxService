package com.zm.piboxservice.activity;

import com.zm.pibox.model.LightConfiguration;
import com.zm.piboxservice.component.DefaultComponent;
import com.zm.piboxservice.database.Configuration;
import com.zm.piboxservice.rpi.PinState;
import com.zm.piboxservice.sensor.Photoresistor;

import java.time.LocalTime;

public class Illumination implements Activity {

    private LightConfiguration _configuration;
    private DefaultComponent _light;
    private Photoresistor _photoresistor;

    public Illumination(LightConfiguration configuration, DefaultComponent light, Photoresistor photoresistor) {
        _configuration = configuration;
        _light = light;
        _photoresistor = photoresistor;
    }

    public void setLightSchedule(LightConfiguration schedule) {
        _configuration = Configuration.getOrDefault(schedule);

    }

    @Override
    public void call() {

        /*
         * Consider the two cases:
         *
         *     1. start_time <= end_time
         *     2. start_time > end_time
         *
         * During the first, the light is ON when the current time is after the start_time and before
         * the end_time.  The second is more complicated because the light is ON during the change of a new day.
         * In this case, the light is OFF when the current time is after the end_time and before the start_time.
         */



        //TODO: Timezones need to be introduced, otherwise this will be the browser vs the computer timezone
        LocalTime now = LocalTime.now();
        LocalTime start = _configuration.getOn().toLocalTime();
        LocalTime end = _configuration.getOff().toLocalTime();
        PinState state = !start.isAfter(end) ?
                now.isAfter(start) && now.isBefore(end) ? PinState.HIGH : PinState.LOW :
                !(now.isAfter(end) && now.isBefore(start)) ? PinState.HIGH : PinState.LOW;

        setState(state);
        _photoresistor.setLight(_light.getState() == PinState.HIGH);
    }

    @Override
    public void pause() {
        _light.override(_light.getState());
    }

    @Override
    public void stop() {
        _light.override(PinState.LOW);
    }

    @Override
    public void start() {
        _light.removeOverride();
    }

    @Override
    public void recover() {}

    private void setState(PinState state) {
        switch (state) {
            case HIGH:
                _light.turnOn();
                break;

            case LOW:
                _light.turnOff();
                break;
        }
    }
}
