package com.aopanis.forgetmenot.helpers;

/**
 * Created by malcolm on 7/7/2017.
 */

public class GPSHelper {
    private static StringBuilder stringBuilder = new StringBuilder(20);

    public static String latitudeRef(double latitude) {
        return latitude < 0.0d ? "S":"N";
    }

    public static String longitudeRef(double longitude){
        return longitude < 0.0d ? "W":"E";
    }

    /**
     * Convert latitude into DMS (degree minute second) format. For instance<br/>
     * It works for latitude and longitude
     * @param coordinate can be latitude or longitude.
     * @return correct DMS format
     */
    synchronized public static final String convert(double coordinate){
        coordinate=Math.abs(coordinate);
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

}
