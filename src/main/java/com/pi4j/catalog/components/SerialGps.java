package com.pi4j.catalog.components;

import java.util.Locale;
import java.util.function.Consumer;

import com.pi4j.context.Context;

import com.pi4j.catalog.components.base.Component;
import com.pi4j.catalog.components.base.SerialDevice;

public class SerialGps extends Component {

    private final Consumer<GeoPosition> onNewPosition;
    private final SerialDevice device;

    public SerialGps(Context pi4j, Consumer<GeoPosition> onNewPosition) {
        this.onNewPosition = onNewPosition;
        device = new SerialDevice(pi4j, (line) -> handleNewData(line));
    }


    public void start() {
        device.startReading();
        logInfo("reading GPS data started");
    }

    public void stop() {
        device.stopReading();
        logInfo("Stopped reading GPS data");
    }

    @Override
    public void reset() {
        device.reset();
        super.reset();
        logInfo("Stopped reading GPS data");
    }

    private void handleNewData(String line) {
        String[] data = line.split(",");
        if (data[0].equals("$GPGLL")) {
            System.out.println(line);
            double latitude = 0;
            if(!data[1].isEmpty()){
                int degree = Integer.parseInt(data[1].substring(0, 2));
                double minutes = Double.parseDouble(data[1].substring(2));
                latitude = degree + (minutes / 60.0);
            }

            double longitude = 0;
            if(!data[3].isEmpty()){
                int degree = Integer.parseInt(data[3].substring(0, 3));
                double minutes = Double.parseDouble(data[3].substring(3));
                longitude = degree + (minutes / 60.0);
            }

            if (latitude != 0 && data[2].equals("S")) {
                latitude = -latitude;
            }
            if (longitude != 0 && data[2].equals("W")) {
                longitude = -longitude;
            }
            GeoPosition pos = new GeoPosition(latitude, longitude);
            onNewPosition.accept(pos);
        }
    }

    public record GeoPosition(double latitude, double longitude) {

        public String dms() {
                return format(latitude, longitude);
            }

            private String format(double latitude, double longitude) {
                String latCompassDirection = (latitude > 0.0) ? "N" : "S";
                String lonCompassDirection = (longitude > 0.0) ? "E" : "W";

                return String.format("%s%s, %s%s", getDMS(latitude), latCompassDirection, getDMS(longitude), lonCompassDirection);
            }

            private String getDMS(double value) {
                double absValue = Math.abs(value);
                int degree      = (int) absValue;
                int minutes     = (int) ((absValue - degree) * 60.0);
                double seconds  = (int) ((absValue - degree - minutes / 60.0) * 3600.0);

                return String.format("%d°%d′%s″", degree, minutes, String.format(Locale.ENGLISH, "%.1f", seconds));
            }
        }
}


