package com.pi4j.catalog.components.base;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.function.Consumer;

import com.pi4j.boardinfo.util.BoardInfoHelper;

import com.fazecast.jSerialComm.SerialPort;

public class SerialSensor extends Component {
    private final SerialPort port;

    public SerialSensor(int baudRate, Consumer<String> onNewLine) {
        Objects.requireNonNull(onNewLine);

        port = createPort(baudRate);
        openPort(port, onNewLine);
    }

    public void shutdown() {
        super.shutdown();
        if (BoardInfoHelper.runningOnRaspberryPi()) {
            port.closePort();
        }
    }

    private SerialPort createPort(int baudRate) {
        if (BoardInfoHelper.runningOnRaspberryPi()) {
            SerialPort port = SerialPort.getCommPorts()[0];
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0); //no read timeout
            port.setComPortParameters(baudRate, 8, 1, SerialPort.NO_PARITY);     // Set baud rate, data bits, stop bits, and parity

            return port;
        } else {
            return null;
        }
    }

    private void openPort(SerialPort port, Consumer<String> onNewLine) {
        if (BoardInfoHelper.runningOnRaspberryPi()) {
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

}
