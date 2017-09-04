/*
 * Copyright (C) 2017 zmiller
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.zm.piboxservice;

import com.zm.pblp.model.GeneralLog;
import com.zm.pblp.client.LogClient;
import com.zm.pblp.configuration.LogChannel;
import com.zm.pblp.configuration.LogHost;
import com.zm.pibox.API;
import com.zm.pibox.configuration.PiChannel;
import com.zm.pibox.configuration.PiHost;
import com.zm.rabbitmqservice.RMQApplicationException;
import com.zm.rabbitmqservice.RMQApplication;
import com.zm.rpibox.model.ComponentMessage;
import java.util.UUID;

/**
 *
 * @author zmiller
 */
public class PiBox implements API {
    
    private final LogClient logClient;
    PiBox() throws Exception {
         logClient = new LogClient(LogHost.LOCAL, LogChannel.LOGS, 5);
    }
    
    private static void daemonize(Runnable r) {
        Thread t = new Thread(r);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public void updateLight(ComponentMessage message) {
        throw new RMQApplicationException(123, "test");
    }

    @Override
    public Void testlog(String message) {
        try {
            
            GeneralLog log = new GeneralLog();
            log.setId(UUID.randomUUID().toString());
            log.setMessage(message);
            log.setTime(System.currentTimeMillis());
            logClient.log(log);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }

    @Override
    public String testString(ComponentMessage message) {
        return "Hello!";
    }
    
    public static void main(String[] argv) throws Exception {
        String host = PiHost.LOCAL.getValue();
        String channel = PiChannel.TEST.getValue();
        RMQApplication.start(host, channel, new PiBox(), API.class, 5);
    }
}
