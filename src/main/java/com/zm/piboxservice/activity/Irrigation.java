package com.zm.piboxservice.activity;

import com.zm.pibox.model.IrrigationConfiguration;
import com.zm.piboxservice.database.Configuration;
import com.zm.piboxservice.sensor.Moisture;

public class Irrigation implements Activity {

    private Moisture _moisture;
    private IrrigationConfiguration _configuration;

    public Irrigation(IrrigationConfiguration configuration, Moisture moisture) {
        _configuration = configuration;
        _moisture = moisture;
    }

    public void setIrrigationConfiguration(IrrigationConfiguration configuration) {
        _configuration = Configuration.getOrDefault(configuration);
    }

    @Override
    public void call() {
        double density = _moisture.read();
        if(density < _configuration.getMinimum()) {
            _moisture.resetDensity();
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
