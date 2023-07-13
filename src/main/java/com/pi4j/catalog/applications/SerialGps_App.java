package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.SerialGps;

public class SerialGps_App implements Application {
    @Override
    public void execute(Context pi4j) {
        //there's nothing special about the GPS module. It just sends some data via the serial port
        //We can use the standard 'SerialDevice' and configure it with the appropr
        SerialGps gps = new SerialGps(pi4j,
                (geoPosition) -> System.out.println(geoPosition.dms()));

        gps.start();

        delay(Duration.ofSeconds(10));

        gps.reset();

        System.out.println("Serial is no longer open");
    }
}
