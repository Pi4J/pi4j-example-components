package com.pi4j.catalog.components;

import java.util.Locale;
import java.util.function.Consumer;

import com.pi4j.context.Context;

import com.pi4j.catalog.components.base.Component;
import com.pi4j.catalog.components.base.SerialDevice;

/**
 * This component sends GPS information as NMEA sentence over the serial UART bus.
 * <p>
 * see <a href="http://aprs.gids.nl/nmea/">GPS - NMEA sentence information</a>
 * <p>
 * There's nothing special about the GPS module. It just sends some data via the serial port
 * We can use the standard 'SerialDevice' and configure it appropriately.
 *
 * SerialGps just converts the Strings delivered by SerialReader to Positions, consisting of longitude, latitude, altitude
 *
 */
public class SerialGps extends Component {
    //only if the sensor has moved significantly, the new position will be reported
    private static final double MIN_DISTANCE_M = 1.0;

    private final SerialDevice device;

    private final Consumer<GeoPosition> onNewPosition;
    private final Consumer<Double>      onNewAltitude;

    private int numberOfSatellites = 0;

    private GeoPosition lastReportedPosition = new GeoPosition(0,0);
    private double      lastReportedAltitude = -999;


    /**
     *
     * @param pi4j the good old Pi4J context
     * @param onNewPosition will be called if device has moved significantly
     * @param onNewAltitude will be called if device has a new altitude
     */
    public SerialGps(Context pi4j,
                     Consumer<GeoPosition> onNewPosition,
                     Consumer<Double>      onNewAltitude
                     ) {
        this.onNewPosition = onNewPosition;
        this.onNewAltitude = onNewAltitude;
        device = new SerialDevice(pi4j, this::handleNewData);
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

    /**
     * When a new line is delivered by SerialReader, SerialGPS has to convert it in a new GeoPosition and altitude.
     * <p>
     * For the delivered Strings see <a href="http://aprs.gids.nl/nmea/">GPS - NMEA sentence information</a>
     *
     * @param line the String delivered by the SerialReader
     */
    private void handleNewData(String line) {
        logDebug("Serial reader delivered: '%s'", line);
        String[] data = line.split(",");
        switch (data[0]) {
            case "$GPGGA" -> handleFixData(data);
            case "$GPGLL" -> handlePosition(data[1], data[2], data[3], data[4]);
        }
    }

    private void handleFixData(String[] data) {
        try {
            String satelliteString = data[7];
            if(satelliteString.contains(".")){
                satelliteString = satelliteString.substring(0, satelliteString.indexOf('.'));
            }
            numberOfSatellites = satelliteString.isEmpty() ? 0 : Integer.parseInt(satelliteString);
            logDebug("Number of satellites in use: %d", numberOfSatellites);
            handlePosition(data[2], data[3], data[4], data[5]);
            handleAltitude(data[9]);
        } catch (Exception e) {
            logError("unknown NMEA sentence: '$GPGGA, %s'", String.join(",", data));
        }
    }

    private void handleAltitude(String altitudeString) {
        if(numberOfSatellites >= 3 && onNewAltitude != null && !altitudeString.isEmpty()){
            double altitude = Double.parseDouble(altitudeString);
            if(Math.abs(altitude - lastReportedAltitude) >= MIN_DISTANCE_M){
                lastReportedAltitude = altitude;
                logDebug("Current altitude, %.1f m", altitude);
                onNewAltitude.accept(altitude);
            }
        }
    }

    private void handlePosition(String lat, String northOrSouth, String lng, String eastOrWest){
        if(numberOfSatellites >=3 && onNewPosition != null){
            double latitude = 0;
            if (!lat.isEmpty()) {
                int degree = Integer.parseInt(lat.substring(0, 2));
                double minutes = Double.parseDouble(lat.substring(2));
                latitude = degree + (minutes / 60.0);
            }
            double longitude = 0;
            if (!lng.isEmpty()) {
                int degree = Integer.parseInt(lng.substring(0, 3));
                double minutes = Double.parseDouble(lng.substring(3));
                longitude = degree + (minutes / 60.0);
            }
            if (latitude != 0 && northOrSouth.equals("S")) {
                latitude = -latitude;
            }
            if (longitude != 0 && eastOrWest.equals("W")) {
                longitude = -longitude;
            }

            GeoPosition pos =  new GeoPosition(latitude, longitude);

            double moved = lastReportedPosition.distance(pos);
            logDebug("Moved %.2f m", moved);
            if(moved >= MIN_DISTANCE_M){
                logDebug("GPS: new position: %s", pos.dms());
                lastReportedPosition = pos;
                onNewPosition.accept(pos);
            }
            else {
                logDebug("No significant movement");
            }
        }

    }

    public record GeoPosition(double latitude, double longitude) {

        public String dms() {
            return format(latitude, longitude);
        }

        /**
         * Just a simplified version of distance calculation. Doesn't take the different altitudes into account.
         *
         * @param otherPosition the position
         * @return distance in meter
         */
        public double distance(GeoPosition otherPosition){
            double lon1 = longitude;
            double lon2 = otherPosition.longitude;
            double lat1 = latitude;
            double lat2 = otherPosition.latitude;
            double theta = lon1 - lon2;

            return  rad2deg(Math.acos(Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta)))) * 60 * 1.1515 * 1609.344;
        }

        /**
         * converts decimal degrees to radians
         */
        private double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
        }

        /**
         * converts radians to decimal degrees
         */
         private double rad2deg(double rad) {
            return (rad * 180.0 / Math.PI);
        }

        private String format(double latitude, double longitude) {
            String latCompassDirection = (latitude > 0.0)  ? "N" : "S";
            String lonCompassDirection = (longitude > 0.0) ? "E" : "W";

            return String.format("%s%s, %s%s", getDMS(latitude), latCompassDirection, getDMS(longitude), lonCompassDirection);
        }

            private String getDMS(double value) {
                double absValue = Math.abs(value);
                int degree      = (int) absValue;
                int minutes     = (int) ((absValue - degree) * 60.0);
                double seconds  = (absValue - degree - minutes / 60.0) * 3600.0;

            return String.format("%d°%d′%s″", degree, minutes, String.format(Locale.ENGLISH, "%.4f", seconds));
        }
    }
}


