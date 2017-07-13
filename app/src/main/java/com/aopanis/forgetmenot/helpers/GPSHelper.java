package com.aopanis.forgetmenot.helpers;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

/**
 * Created by malcolm on 7/7/2017.
 */

public class GPSHelper {
    private static StringBuilder stringBuilder = new StringBuilder(20);

    public static String latitudeRefDtS(double latitude) {
        return latitude < 0.0d ? "S":"N";
    }
    public static String longitudeRef(double longitude){
        return longitude < 0.0d ? "W":"E";
    }
    public static double latitudeRefStD(String latitude) {
        switch(latitude) {
            case "S":
                return -1d;
            case "N":
                return 1d;
            default:
                return 0d;
        }
    }
    public static double longitudeRegStD(String longitude) {
        switch(longitude) {
            case "W":
                return -1d;
            case "E":
                return 1d;
            default:
                return 0d;
        }
    }

    /**
     * Convert latitude into DMS (degree minute second) format. For instance<br/>
     * It works for latitude and longitude
     * @param coordinate can be latitude or longitude.
     * @return Correct DMS format
     */
    synchronized public static final String convertToDms(double coordinate){
        coordinate = Math.abs(coordinate);
        int degree = (int) coordinate;
        coordinate *= 60;
        coordinate -= (degree * 60.0d);
        int minute = (int) coordinate;
        coordinate *= 60;
        coordinate -= (minute * 60.0d);
        int second = (int) (coordinate * 1000.0d);

        stringBuilder.setLength(0);
        stringBuilder.append(degree);
        stringBuilder.append("/1,");
        stringBuilder.append(minute);
        stringBuilder.append("/1,");
        stringBuilder.append(second);
        stringBuilder.append("/1000,");
        return stringBuilder.toString();
    }
    /**
     * Convert the DMS notation of a latitude or longitude to a double degrees
     * @param stringDMS the DMS form of the latitude or longitude
     * @return The latitude or longitude in degrees
     */
    synchronized public static final double convertToDegree(String stringDMS){
        double result;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        double D0 = new Double(stringD[0]);
        double D1 = new Double(stringD[1]);
        double degrees = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        double M0 = new Double(stringM[0]);
        double M1 = new Double(stringM[1]);
        double minutes = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        double S0 = new Double(stringS[0]);
        double S1 = new Double(stringS[1]);
        double seconds = S0 / S1;

        result = degrees + (minutes / 60) + (seconds / 3600);

        return result;
    }

    /**
     * Convert a LatLng and a radius to a LatLngBounds
     * @param center the LatLng representation of the center
     * @param radius the radius to use
     * @return The LatLngBounds object desired
     */
    public static final LatLngBounds toBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }
    /**
     * Convert a latitude, longitude and a radius to a LatLngBounds
     * @param lat the latitude of the center
     * @param lon the longitude of the center
     * @param radius the radius to use
     * @return The LatLngBounds object desired
     */
    public static final LatLngBounds toBounds(double lat, double lon, double radius) {
        return toBounds(new LatLng(lat, lon), radius);
    }
}
