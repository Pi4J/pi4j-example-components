package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import com.pi4j.catalog.components.SerialGps;

public class SerialGpsApp {

    public static void main(String[] args) {
        final Context pi4j = Pi4J.newAutoContext();

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

    /**
     * Utility function to sleep for the specified amount of milliseconds.
     * An {@link InterruptedException} will be catched and ignored while setting the interrupt flag again.
     *
     * @param duration Time to sleep
     */
    private static void delay(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
