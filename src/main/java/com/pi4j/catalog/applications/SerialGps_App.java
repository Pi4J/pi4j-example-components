package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.SerialGps;

public class SerialGps_App implements Application {
    @Override
    public void execute(Context pi4j) {
        System.out.println("GPS demo started");
        SerialGps gps = new SerialGps(pi4j,
                (geoPosition) -> System.out.printf("Position: %.6f, %.6f; DMS: %s%n", geoPosition.latitude(), geoPosition.longitude(), geoPosition.dms() ),
                (altitude)    -> System.out.printf("Altitude: %.1fm%n", altitude));

        gps.start();

        //provide geopositions for 15 sec
        delay(Duration.ofSeconds(15));

        gps.reset();

        System.out.println("GPS demo finished");
    }
}
