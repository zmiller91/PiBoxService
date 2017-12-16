package com.zm.piboxservice;

import com.zm.pibox.PiBoxClient;
import com.zm.pibox.configuration.PiBoxConfiguration;
import com.zm.pibox.model.HVACConfiguration;
import com.zm.pibox.model.IrrigationConfiguration;
import com.zm.pibox.model.LightConfiguration;
import com.zm.piboxservice.activity.HVAC;
import com.zm.rabbitmqservice.ClientException;

import java.sql.Time;

public class RunClient {

    public static void main(String[] args) throws Exception{

        PiBoxClient client = new PiBoxClient(PiBoxConfiguration.Host.TEST, PiBoxConfiguration.Channel.TEST, 10);
        client.setClientTimeout(2000);
        client.setMessageExpiry(2000);

        LightConfiguration light = new LightConfiguration();
        light.setOn(Time.valueOf("11:30:00"));
        light.setOff(Time.valueOf("23:04:00"));

        HVACConfiguration hvac = new HVACConfiguration();
        hvac.setMinimum(70);
        hvac.setMaximum(75);

        IrrigationConfiguration irrigation = new IrrigationConfiguration();
        irrigation.setMinimum(20);

        try {

            light.setOn(null);
            light.setOff(null);
            client.putIrrigationConfiguration(null);
            client.putHVACConfiguration(null);
            client.putLightConfiguration(null);
//            client.removeComponentOverride("light");
            client.overrideComponentOn("light");
            int a = 1;
        }
        catch(ClientException ce) {
            System.out.println(ce.message);
            System.out.println(ce.cause.getMessage());
//            ce.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            client.close();
        }

    }

}
