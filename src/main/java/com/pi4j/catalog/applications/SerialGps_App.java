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
                (pos) -> System.out.printf("Position: %.6f, %.6f; DMS: %s%n", pos.latitude(), pos.longitude(), pos.dms() ),
                (alt) -> System.out.printf("Altitude: %.1fm%n", alt));

        gps.start();

        //provide positions for 15 sec
        delay(Duration.ofSeconds(15));

        gps.reset();

        System.out.println("GPS demo finished");
    }
}
