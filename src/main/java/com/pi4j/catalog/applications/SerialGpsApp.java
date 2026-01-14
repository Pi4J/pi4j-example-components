package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.catalog.components.base.Component;
import com.pi4j.catalog.components.SerialGps;

public class SerialGpsApp {

    public static void main(String[] args) {
        System.out.println("GPS demo started");

        SerialGps gps = new SerialGps(pos -> System.out.printf("Position: %.6f, %.6f; DMS: %s%n", pos.latitude(), pos.longitude(), pos.dms() ),
                                      alt -> System.out.printf("Altitude: %.1fm%n", alt));


        //provide positions for 15 sec
        Component.delay(Duration.ofSeconds(60));

        gps.shutdown();

        System.out.println("GPS demo finished");
    }
}
