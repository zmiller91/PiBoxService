package com.zm.piboxservice.activity;

import com.zm.pibox.model.IrrigationConfiguration;
import com.zm.piboxservice.database.Configuration;
import com.zm.piboxservice.sensor.Moisture;
import com.zm.piboxservice.sensor.Sensor;
import com.zm.piboxservice.sensor.SensorListener;

public class Irrigation implements Activity, SensorListener {

    private IrrigationConfiguration _configuration;

    public Irrigation(IrrigationConfiguration configuration) {
        _configuration = configuration;
    }

    public void setIrrigationConfiguration(IrrigationConfiguration configuration) {
        _configuration = Configuration.getOrDefault(configuration);
    }

    @Override
    public void onSensorUpdate(Sensor sensor) {
        if(sensor instanceof Moisture) {
            Moisture moisture = (Moisture) sensor;
            double density = moisture.read();
            if (density < _configuration.getMinimum()) {
                moisture.resetDensity();
            }
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
