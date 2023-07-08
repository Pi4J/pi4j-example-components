package com.pi4j.catalog.applications;

import java.time.Duration;

import com.pi4j.context.Context;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.StopBits;
import com.pi4j.util.Console;

import com.pi4j.catalog.Application;
import com.pi4j.catalog.components.SerialReader;
import com.pi4j.catalog.components.helpers.PrintInfo;

public class SerialGps_App implements Application {
    @Override
    public void execute(Context pi4j) {

        // Create Pi4J console wrapper/helper
        // (This is a utility class to abstract some boilerplate stdin/stdout code)
        var console = new Console();

        // Print program title/header
        console.title("<-- The Pi4J Project -->", "Serial Example project");

        // ------------------------------------------------------------
        // Output Pi4J Context information
        // ------------------------------------------------------------
        // The created Pi4J Context initializes platforms, providers
        // and the I/O registry. To help you to better understand this
        // approach, we print out the info of these. This can be removed
        // from your own application.
        // OPTIONAL
        PrintInfo.printLoadedPlatforms(console, pi4j);
        PrintInfo.printDefaultPlatform(console, pi4j);
        PrintInfo.printProviders(console, pi4j);

        // Here we will create I/O interface for the serial communication.
        Serial serial = pi4j.create(Serial.newConfigBuilder(pi4j)
                .use_9600_N81()
                .dataBits_8()
                .parity(Parity.NONE)
                .stopBits(StopBits._1)
                .flowControl(FlowControl.NONE)
                .id("my-serial")
                .device("/dev/ttyS0")
                .provider("pi-gpio-serial")
                .build());
        serial.open();

        // Wait till the serial port is open
        console.print("Waiting till serial port is open");
        while (!serial.isOpen()) {
            console.print(".");
            delay(Duration.ofMillis(250));
        }
        console.println("");
        console.println("Serial port is open");

        // OPTIONAL: print the registry
        PrintInfo.printRegistry(console, pi4j);

        // Start a thread to handle the incoming data from the serial port
        SerialReader serialReader = new SerialReader(console, serial);
        Thread serialReaderThread = new Thread(serialReader, "SerialReader");
        serialReaderThread.setDaemon(true);
        serialReaderThread.start();

        while (serial.isOpen()) {
            delay(Duration.ofMillis(500));
        }

        serialReader.stopReading();

        console.println("Serial is no longer open");

    }
}
