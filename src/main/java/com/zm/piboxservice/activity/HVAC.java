package com.zm.piboxservice.activity;

import com.zm.pibox.model.HVACConfiguration;
import com.zm.piboxservice.database.Configuration;
import com.zm.piboxservice.sensor.Temperature;

public class HVAC implements Activity {

    long time = 0;
    private Temperature _temp;
    private HVACConfiguration _configuration;

    public HVAC(HVACConfiguration configuration, Temperature temp) {
        _configuration = configuration;
        _temp = temp;
    }

    public void setHVACConfiguration(HVACConfiguration configuration) {
        _configuration = Configuration.getOrDefault(configuration);
    }

    @Override
    public void call() {
        double t = _temp.read();
        _temp.usingHeater(t < _configuration.getMinimum());
        _temp.usingAC(_configuration.getMaximum() < t);
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
