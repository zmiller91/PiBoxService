package com.zm.piboxservice.sensor;

import com.zm.piboxservice.sensor.Sensor;

public interface SensorListener {
    void onSensorUpdate(Sensor sensor);
}
