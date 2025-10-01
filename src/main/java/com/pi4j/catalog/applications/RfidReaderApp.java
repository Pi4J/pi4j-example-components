package com.pi4j.catalog.applications;

import com.pi4j.Pi4J;
import com.pi4j.catalog.components.RfidReader;
import com.pi4j.context.Context;

public class RfidReaderApp {

    public static void main(String[] args) {
        final Context pi4j = Pi4J.newAutoContext();

        System.out.println("Starting Pi4J RFID Reader demo");

        final RfidReader reader = new RfidReader(pi4j);

        //We register an event listener to detect cards in proximity
        reader.onCardDetected(card -> {
            //Print some informations
            System.out.println("Detected card with serial number: " + card.getSerial() + " and capacity: " + card.getCapacity() + " bytes.");

        });
    }
}