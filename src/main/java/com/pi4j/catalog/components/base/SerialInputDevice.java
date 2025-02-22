package com.pi4j.catalog.components.base;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.function.Consumer;

import com.pi4j.boardinfo.util.BoardInfoHelper;

import com.fazecast.jSerialComm.SerialPort;

public class SerialInputDevice extends Component {
    private final Consumer<String> onNewLine;
    private SerialPort port;

    public SerialInputDevice(int baudRate, Consumer<String> onNewLine) {
        Objects.requireNonNull(onNewLine);

        this.onNewLine = onNewLine;
        openPort(baudRate);
    }

    private void openPort(int baudRate) {
        if(BoardInfoHelper.runningOnRaspberryPi()){
            port = SerialPort.getCommPorts()[0];
            port.setBaudRate(baudRate);
            port.openPort();
            // Set up an input stream to read from the serial port
            try (BufferedReader input = new BufferedReader(new InputStreamReader(port.getInputStream()))) {
                String line;
                // Continuously read until the port is closed
                while ((line = input.readLine()) != null) {
                    onNewLine.accept(line);
                }
            } catch (Exception e) {
                logException("Reading from Serial Port throws ", e);
            } finally {
                // Always ensure the port is closed after use
                port.closePort();
            }
        }
    }

    public void shutdown() {
        if(BoardInfoHelper.runningOnRaspberryPi()){
            port.closePort();
        }
    }

}
