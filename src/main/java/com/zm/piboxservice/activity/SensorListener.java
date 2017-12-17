package com.zm.piboxservice.activity;

import com.zm.piboxservice.sensor.Sensor;

public interface SensorListener {
    void onSensorUpdate(Sensor sensor);
}
