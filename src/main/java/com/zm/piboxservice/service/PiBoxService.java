package com.zm.piboxservice.service;


import com.zm.pbmessenger.PBMessengerClient;
import com.zm.pbmessenger.configuration.PBMessengerConfiguration;
import com.zm.pibox.PiBoxApi;
import com.zm.pibox.model.HVACConfiguration;
import com.zm.pibox.model.IrrigationConfiguration;
import com.zm.pibox.model.LightConfiguration;
import com.zm.piboxservice.activity.Activity;
import com.zm.piboxservice.activity.HVAC;
import com.zm.piboxservice.activity.Irrigation;
import com.zm.piboxservice.activity.Illumination;
import com.zm.piboxservice.activity.Monitor;
import com.zm.piboxservice.component.Component;
import com.zm.piboxservice.component.DefaultComponent;
import com.zm.piboxservice.database.Configuration;
import com.zm.piboxservice.rpi.GPIO;
import com.zm.piboxservice.rpi.PinState;
import com.zm.piboxservice.rpi.Pinout;
import com.zm.piboxservice.sensor.Moisture;
import com.zm.piboxservice.sensor.Photoresistor;
import com.zm.piboxservice.sensor.Sensor;
import com.zm.piboxservice.sensor.Temperature;
import com.zm.rabbitmqservice.ClientException;
import com.zm.rabbitmqservice.ServiceException;

import java.util.*;

public class PiBoxService implements PiBoxApi, Runnable {

    private Configuration _configuration;
    private PBMessengerClient _pbMessenger;
    private LinkedList<Activity> _activityQueue;
    private LinkedList<Sensor> _sensorQueue;
    private Catalog<Activity> _activityCatalog;
    private Catalog<Component> _componentCatalog;

    public PiBoxService() {

        _configuration = Configuration.instance();
        _pbMessenger = new PBMessengerClient(PBMessengerConfiguration.Host.TEST, PBMessengerConfiguration.Channel.TEST, 10);
        _activityQueue = new LinkedList<>();
        _sensorQueue = new LinkedList<>();
        _activityCatalog = new Catalog<>();
        _componentCatalog = new Catalog<>();

        Monitor monitor = setupMonitor();
        setupIllumination(monitor);
        setupHVAC(monitor);
        setupIrrigation(monitor);
    }

    private Monitor setupMonitor() {
        Monitor monitor = new Monitor();
        _activityCatalog.put("monitor", monitor);
        _activityQueue.add(monitor);
        return monitor;
    }

    private void setupIllumination(Monitor monitor) {
        DefaultComponent light = new DefaultComponent("light", new GPIO(Pinout.LIGHT));
        Photoresistor photoresistor = new Photoresistor(new GPIO(Pinout.PHOTORESISTOR));
        Illumination illumination = new Illumination(_configuration.getLightConfiguration(), light, photoresistor);
        _componentCatalog.put("light", light);
        _activityCatalog.put("illumination", illumination);
        _activityQueue.add(illumination);
        _sensorQueue.add(photoresistor);
        monitor.setPhotoresistor(photoresistor);
    }

    private void setupHVAC(Monitor monitor) {
        Temperature temp = new Temperature(new GPIO(Pinout.TEMPERATURE));
        HVAC hvac = new HVAC(_configuration.getHVACConfiguration(), temp);
        _activityCatalog.put("hvac", hvac);
        _activityQueue.add(hvac);
        _sensorQueue.add(temp);
        monitor.setTemperatureSensor(temp);
    }

    private void setupIrrigation(Monitor monitor) {
        Moisture moisture = new Moisture(new GPIO(Pinout.MOISTURE));
        Irrigation irrigation = new Irrigation(_configuration.getIrrigationConfiguration(), moisture);
        _activityCatalog.put("irrigation", irrigation);
        _activityQueue.add(irrigation);
        _sensorQueue.add(moisture);
        monitor.setMoistureSensor(moisture);
    }

    @Override
    public LightConfiguration putLightConfiguration(LightConfiguration config) throws ServiceException, ClientException {
        config = Configuration.getOrDefault(config);
        Illumination illumination = (Illumination) _activityCatalog.get("illumination");
        if(_configuration.setLightConfiguration(config)) {
            illumination.setLightSchedule(config);
        }

       return _configuration.getLightConfiguration();
    }

    @Override
    public IrrigationConfiguration putIrrigationConfiguration(IrrigationConfiguration config) throws ServiceException, ClientException {
        config = Configuration.getOrDefault(config);
        Irrigation irrigation = (Irrigation) _activityCatalog.get("irrigation");
        if(_configuration.setIrrigationConfiguration(config)) {
            irrigation.setIrrigationConfiguration(config);
        }

        return _configuration.getIrrigationConfiguration();
    }

    @Override
    public HVACConfiguration putHVACConfiguration(HVACConfiguration config) throws ServiceException, ClientException {
        config = Configuration.getOrDefault(config);
        HVAC hvac = (HVAC) _activityCatalog.get("hvac");
        if(_configuration.setHVACConfiguration(config)) {
            hvac.setHVACConfiguration(config);
        }

        return _configuration.getHVACConfiguration();
    }

    @Override
    public void startActivity(String name) throws ServiceException, ClientException {
        Activity activity = _activityCatalog.get(name);
        if(activity != null) {
            activity.start();
        }
    }

    @Override
    public void stopActivity(String name) throws ServiceException, ClientException {
        Activity activity = _activityCatalog.get(name);
        if(activity != null) {
            activity.stop();
        }
    }

    @Override
    public void pauseActivity(String name) throws ServiceException, ClientException {
        Activity activity = _activityCatalog.get(name);
        if(activity != null) {
            activity.pause();
        }
    }

    @Override
    public void overrideComponentOn(String name) throws ServiceException, ClientException {
        Component component = _componentCatalog.get(name);
        if(component != null) {
            component.override(PinState.HIGH);
        }
    }

    @Override
    public void overrideComponentOff(String name) throws ServiceException, ClientException {
        Component component = _componentCatalog.get(name);
        if(component != null) {
            component.override(PinState.LOW);
        }
    }

    @Override
    public void removeComponentOverride(String name) throws ServiceException, ClientException {
        Component component = _componentCatalog.get(name);
        if(component != null) {
            component.removeOverride();
        }
    }

    @Override
    public void run() {
        while(true) {
            callActivity();
            callSensor();
        }
    }

    private void callActivity() {
        Activity activity = _activityQueue.poll();
        try {
            activity.call();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        _activityQueue.add(activity);
    }

    private void callSensor() {
        Sensor sensor = _sensorQueue.poll();
        try {
            sensor.call();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        _sensorQueue.add(sensor);
    }
}
