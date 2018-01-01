package com.zm.piboxservice.activity;

import com.zm.piboxservice.Messenger;
import com.zm.piboxservice.sensor.Moisture;
import com.zm.piboxservice.sensor.Photoresistor;
import com.zm.piboxservice.sensor.Temperature;

import java.util.TimerTask;

public class Monitor extends TimerTask implements Activity {

    private Photoresistor _photoresistor;
    private Moisture _moisture;
    private Temperature _temperature;
    private long time  = 0;

    @Override
    public void run() {
        if(System.currentTimeMillis() - time > 1000){
            StringBuilder sb = new StringBuilder();
            sb.append("-------------------------\n");
            sb.append("Photoresistor:   " + round(_photoresistor.read()) + "\n");
            sb.append("Moisture:        " + round(_moisture.read()) + "\n");
            sb.append("Temperature:     " + round(_temperature.read()) + "\n");
            sb.append("-------------------------");

            Messenger.send(sb.toString());
            time = System.currentTimeMillis();
        }
    }

    private double round(double d) {
        return (double)Math.round(d * 100d) / 100d;
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

    public void setPhotoresistor(Photoresistor photoresistor) {
        _photoresistor = photoresistor;
    }

    public void setMoistureSensor(Moisture moisture) {
        _moisture = moisture;
    }

    public void setTemperatureSensor(Temperature temperature) {
        _temperature = temperature;
    }
}
