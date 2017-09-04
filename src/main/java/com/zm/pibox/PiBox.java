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
package com.zm.pibox;

import com.google.gson.JsonArray;
import com.zm.pibox.model.ComponentMessage;
import com.zm.rabbitmqservice.RMQApplicationException;
import com.zm.rabbitmqservice.RMQApplication;
import com.zm.rabbitmqservice.RMQClient;

/**
 *
 * @author zmiller
 */
public class PiBox implements API {
    
    private final RMQClient client;
    PiBox() throws Exception {
         client = new RMQClient("localhost", "logs", 5);
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
        System.out.println(message);
        try {
            JsonArray params = new JsonArray();
            params.add(message);
            System.out.println(client.call("log", params));
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return null;
    }

    @Override
    public String testString(ComponentMessage message) {
        System.out.println("HA");
        return "Hello!";
    }
    
    public static void main(String[] argv) throws Exception {
        RMQApplication.start("localhost", "test", new PiBox(), API.class, 5);
    }
}
