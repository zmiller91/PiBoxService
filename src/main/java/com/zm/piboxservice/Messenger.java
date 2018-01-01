package com.zm.piboxservice;

import com.zm.pbmessenger.PBMessengerClient;
import com.zm.pbmessenger.configuration.PBMessengerConfiguration.Host;
import com.zm.pbmessenger.configuration.PBMessengerConfiguration.Channel;
import com.zm.pbmessenger.model.GeneralLog;
import com.zm.rabbitmqservice.service.ServiceUnavailableException;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.*;

public class Messenger extends TimerTask {

    private static final ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<>();
    private final ThreadLocal<GeneralLog> localLog = new ThreadLocal<>();
    private static final PBMessengerClient client = new PBMessengerClient(Host.TEST, Channel.TEST);

    static {
        Timer timer = new Timer();
        for(int i = 0; i < 5; i++) {
            timer.schedule(new Messenger(), 0, 500);
        }
    }

    private Messenger() {}

    public static void send(String message) {
        messages.add(message);
    }

    @Override
    public void run() {

        if (localLog.get() == null) {
            localLog.set(new GeneralLog());
        }

        try {
            localLog.get().setMessage(messages.poll());
            if(localLog.get().getMessage() != null) {
                localLog.get().setId(UUID.randomUUID().toString());
                localLog.get().setTime(System.currentTimeMillis());
                client.log(localLog.get());
            }
        } catch (ServiceUnavailableException | TimeoutException | IOException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Failed to send: " + e.getMessage());
        }
    }
}
