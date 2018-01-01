package com.zm.piboxservice.activity;

import com.zm.pibox.model.HVACConfiguration;
import com.zm.piboxservice.database.Configuration;
import com.zm.piboxservice.sensor.Sensor;
import com.zm.piboxservice.sensor.SensorListener;
import com.zm.piboxservice.sensor.Temperature;

public class HVAC implements Activity, SensorListener {

    private HVACConfiguration _configuration;

    public HVAC(HVACConfiguration configuration) {
        _configuration = configuration;
    }

    public void setHVACConfiguration(HVACConfiguration configuration) {
        _configuration = Configuration.getOrDefault(configuration);
    }

    @Override
    public void onSensorUpdate(Sensor sensor) {
        if(sensor instanceof Temperature) {
            Temperature temp = (Temperature) sensor;
            double t = sensor.read();
            temp.usingHeater(t < _configuration.getMinimum());
            temp.usingAC(_configuration.getMaximum() < t);
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void start() {

    }

    @Override
    public void recover() {

    }
}
