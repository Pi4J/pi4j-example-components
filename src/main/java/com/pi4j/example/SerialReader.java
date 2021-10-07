package com.pi4j.example;

import com.pi4j.io.serial.Serial;
import com.pi4j.util.Console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SerialReader implements Runnable {

    private final Console console;
    private final Serial serial;

    public SerialReader(Console console, Serial serial) {
        this.console = console;
        this.serial = serial;
    }

    @Override
    public void run() {
        // temperature readings are sent line based, so we use a BufferedReader to read line by line
        BufferedReader br = new BufferedReader(new InputStreamReader(serial.getInputStream()));

        try {
            String line = "";

            // read data until there's an empty line (=broken connection or so)
            while (line != null) {
                // !!! VERY IMPORTANT !!! Data can only be read as long as there is some data to read. So check for available data first, every time!
                // The read() command for pigio-serial is a NON-BLOCKING call, in contrast to typical java input streams ...
                if (serial.available() > 0) {
                    line = br.readLine();
                    console.println("Date received from serial: '" + line + "'");
                } else {
                    Thread.sleep(10);
                }
            }
        } catch (Exception e) {
            console.println("Error reading data from serial: " + e.getMessage());
        }
    }
}