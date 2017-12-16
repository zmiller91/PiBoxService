package com.zm.piboxservice;

import com.zm.pbmessenger.PBMessengerClient;
import com.zm.pbmessenger.configuration.PBMessengerConfiguration.Host;
import com.zm.pbmessenger.configuration.PBMessengerConfiguration.Channel;
import com.zm.pbmessenger.model.GeneralLog;
import com.zm.rabbitmqservice.ServiceUnavailableException;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class Messenger {

    private static Messenger instance;
    private final PBMessengerClient client;

    public static Messenger instance() {

        if(instance == null) {
            instance = new Messenger();
        }

        return instance;
    }

    private Messenger() {
        client = new PBMessengerClient(Host.TEST, Channel.TEST, 10);
    }

    public void log(String s) {
        try {

            GeneralLog log = new GeneralLog();
            log.setId(UUID.randomUUID().toString());
            log.setMessage(s);
            log.setTime(System.currentTimeMillis());

            new Thread(() -> {
                try {
                    client.log(log);
                } catch (ServiceUnavailableException | TimeoutException | IOException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("Failed to send: " + e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            System.out.println("Failed to send: " + e.getMessage());
        }
    }

}
